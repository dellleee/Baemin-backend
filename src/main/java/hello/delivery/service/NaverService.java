package hello.delivery.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hello.delivery.Exception.UserNotFoundException;
import hello.delivery.auth.jwt.AuthTokenGenerator;
import hello.delivery.dto.login.LoginResponseDto;
import hello.delivery.dto.login.NaverTokenDto;
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
public class NaverService {

    private final UserRepository userRepository;
    private final AuthTokenGenerator authTokenGenerator;
    private final RestTemplate restTemplate;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverSecret;


    public LoginResponseDto naverLogin(String code,String state) {  //state는 CSRF를 방지하기 위한 인증값
        String naverAccessToken = getNaverAccessToken(code,state);  //인가코드로 네이버액세스토큰 가져오기
        HashMap<String, Object> naverInfo = getNaverInfo(naverAccessToken);  //액세스토큰으로 유저정보 가져오기
        LoginResponseDto naverUserResponse = getNaverUserLogin(naverInfo);  //로그인,회원가입 처리

        return naverUserResponse;
    }

    private String getNaverAccessToken(String code,String state) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverSecret);
        params.add("code", code);
        params.add("state", state);

        HttpEntity<LinkedMultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> tokenResponse = restTemplate.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        NaverTokenDto naverTokenDto = null;

        try {
            naverTokenDto = objectMapper.readValue(tokenResponse.getBody(), NaverTokenDto.class);
            log.info("naverTokenDto = {}", naverTokenDto);
        } catch (JsonProcessingException e) {
            log.error("네이버 Token 받아오기 실패");
            e.printStackTrace();
        }
        return naverTokenDto.getAccessToken();
    }

    private HashMap<String,Object> getNaverInfo(String naverAccessToken) {
        HashMap<String, Object> userInfo = new HashMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + naverAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverUserInfoRequest,
                String.class);

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
            log.info("naver UserInfo={}", jsonNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String email = jsonNode.get("response").get("email").asText();
        String nickname = jsonNode.get("response").get("nickname").asText();

        userInfo.put("email", email);
        userInfo.put("nickname", nickname);

        return userInfo;
    }

    @Transactional
    public LoginResponseDto getNaverUserLogin(HashMap<String, Object> naverInfo) {
        String naverEmail = naverInfo.get("email").toString();
        String nickname = naverInfo.get("nickname").toString();

        User naverUser = userRepository.findByEmail(naverEmail).orElse(null);

        if (naverUser == null) {
            User user = User.builder()
                    .userName(nickname)
                    .email(naverEmail)
                    .loginType("naver")
                    .role(Role.USER)
                    .userGrade("고마운 분")
                    .status("일반").build();
            userRepository.save(user);
        }
        User tokenUser = userRepository.findByEmail(naverEmail).orElseThrow(() ->
                new UserNotFoundException("토큰을 발급할 네이버 유저를 찾을 수 없습니다"));
        Long id = tokenUser.getId();
        AuthToken token = authTokenGenerator.generate(id);
        return new LoginResponseDto(id, nickname, naverEmail, token);
    }


}
