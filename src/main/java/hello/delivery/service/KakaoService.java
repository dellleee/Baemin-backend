package hello.delivery.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hello.delivery.dto.login.KakaoTokenDto;
import hello.delivery.dto.login.LoginResponseDto;
import hello.delivery.entity.Role;
import hello.delivery.entity.User;
import hello.delivery.oauth2.jwt.AuthTokenGenerator;
import hello.delivery.oauth2.jwt.JwtTokenProvider;
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
public class KakaoService {

    private final UserRepository userRepository;
    private final AuthTokenGenerator authTokenGenerator;
    private final JwtTokenProvider jwtTokenProvider;


    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.client.secret}")
    private String kakaoSecret;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    @Value("${kakao.token.uri}")
    private String kakaoTokenUri;

    @Transactional
    public LoginResponseDto<KakaoTokenDto> kakaoLogin(String code, String currentDomain) {
        String kakaoAccessToken = getKakaoAccessToken(code);
        HashMap<String, Object> kakaoInfo = getKakaoInfo(kakaoAccessToken);
        LoginResponseDto<KakaoTokenDto> kakaoUserResponse = kakaoUserLogin(kakaoInfo);

        return kakaoUserResponse;
    }


    private String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");  //카카오 공식 문서 기준 authorization_code로 고정
        params.add("client_id", kakaoClientId);
        params.add("client_secret", kakaoSecret);
        params.add("code", code);
        params.add("redirect_uri", kakaoRedirectUri);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        //카카오로부터 AccessToken 받아오기
        RestTemplate rt = new RestTemplate();  //restTemplate post방식으로 작동함
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                kakaoTokenUri,
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); //LocalDateTime타입의 객체를 직렬/역직렬
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoTokenDto kakaoTokenDto = null;

        try {
            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
            log.info("kakaoTokenDto = {}", kakaoTokenDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return kakaoTokenDto.getAccessToken();
    }

    private HashMap<String,Object> getKakaoInfo(String kakaoAccessToken) {
        HashMap<String, Object> userInfo = new HashMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class);

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String email = jsonNode.get("kakao_acount").get("email").asText();
        String nickname = jsonNode.get("properties").get("nickname").asText();

        userInfo.put("email", email);
        userInfo.put("nickname", nickname);

        return userInfo;

    }

    private LoginResponseDto kakaoUserLogin(HashMap<String, Object> userInfo) {

        String kakaoEmail = userInfo.get("email").toString();
        String nickname = userInfo.get("nickname").toString();

        User kakaoUser = userRepository.findByEmail(kakaoEmail).orElse(null);

        if (kakaoUser == null) {
            User user = User.builder()
                    .userName(nickname)
                    .email(kakaoEmail)
                    .loginType("kakao")
                    .role(Role.USER)
                    .userGrade("고마운 분")
                    .status("일반").build();
            userRepository.save(user);
        }
        return new LoginResponseDto();  //임시
    }
}
