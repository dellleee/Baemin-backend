package hello.delivery.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hello.delivery.Exception.UserNotFoundException;
import hello.delivery.auth.jwt.AuthTokenGenerator;
import hello.delivery.dto.login.GoogleTokenDto;
import hello.delivery.dto.login.LoginResponseDto;
import hello.delivery.entity.AuthToken;
import hello.delivery.entity.Role;
import hello.delivery.entity.User;
import hello.delivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleService {

    private final UserRepository userRepository;
    private final AuthTokenGenerator authTokenGenerator;
    private final RestTemplate restTemplate;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.secret}")
    private String googleSecret;

    public LoginResponseDto googleLogin(String code) {
        String googleAccessToken = getGoogleAccessToken(code);
        HashMap<String, Object> googleInfo = getGoogleInfo(googleAccessToken);
        LoginResponseDto googleUserResponse = getGoogleUserLogin(googleInfo);

        return googleUserResponse;
    }

    private String getGoogleAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", googleClientId);
        params.add("client_secret", googleSecret);
        params.add("code", code);
        params.add("redirect_uri", "http://localhost:3000/login/oauth2/callback/google");

        HttpEntity<LinkedMultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                googleTokenRequest,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        GoogleTokenDto googleTokenDto = null;

        try {
            googleTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), GoogleTokenDto.class);
            log.info("googleTokenDto = {}", googleTokenDto);
        } catch (JsonProcessingException e) {
            log.error("구글 Token 받아오기 실패");
            e.printStackTrace();
        }
        return googleTokenDto.getAccessToken();
    }

    private HashMap<String,Object> getGoogleInfo(String googleAccessToken) {
        HashMap<String, Object> userInfo = new HashMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + googleAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpEntity<MultiValueMap<String, String>> googleUserInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/userinfo/v2/me",
                HttpMethod.GET,
                googleUserInfoRequest,
                String.class);

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try {
            jsonNode = objectMapper.readTree(responseBody);
            log.info("google UserInfo = {}", jsonNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String email = jsonNode.get("email").asText();
        String nickname = jsonNode.get("name").asText();

        userInfo.put("email", email);
        userInfo.put("nickname", nickname);

        return userInfo;
    }

    @Transactional
    public LoginResponseDto getGoogleUserLogin(HashMap<String, Object> userInfo) {
        String googleEmail = userInfo.get("email").toString();
        String nickname = userInfo.get("nickname").toString();

        User googleUser = userRepository.findByEmail(googleEmail).orElse(null);

        if (googleUser == null) {
            User user = User.builder()
                    .userName(nickname)
                    .email(googleEmail)
                    .loginType("google")
                    .role(Role.USER)
                    .userGrade("고마운 분")
                    .status("일반").build();
            userRepository.save(user);
        }
        User tokenUser = userRepository.findByEmail(googleEmail).orElseThrow(() ->
                new UserNotFoundException("토큰을 발급할 구글 유저를 찾을 수 없습니다"));
        Long id = tokenUser.getId();
        AuthToken token = authTokenGenerator.generate(id);
        return new LoginResponseDto(id, nickname, googleEmail, token);
    }
}
