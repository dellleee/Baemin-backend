package hello.delivery.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hello.delivery.Exception.UserNotFoundException;
import hello.delivery.dto.login.KakaoTokenDto;
import hello.delivery.dto.login.LoginResponseDto;
import hello.delivery.entity.AuthToken;
import hello.delivery.entity.Role;
import hello.delivery.entity.User;
import hello.delivery.auth.jwt.AuthTokenGenerator;
import hello.delivery.auth.jwt.JwtTokenProvider;
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
    private final RestTemplate restTemplate;


    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.client.secret}")
    private String kakaoSecret;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    @Value("${kakao.token.uri}")
    private String kakaoTokenUri;

    @Transactional
    public LoginResponseDto kakaoLogin(String code) {
        String kakaoAccessToken = getKakaoAccessToken(code);
        HashMap<String, Object> kakaoInfo = getKakaoInfo(kakaoAccessToken);  //유저정보 가져오기
        LoginResponseDto kakaoUserResponse = kakaoUserLogin(kakaoInfo); //로그인,회원가입

        return kakaoUserResponse;
    }


    /**
     * 카카오서버에서 액세스 토큰 가져오기
     * @param code
     * @return
     */
    private String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders(); // http헤더 생성
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");  //카카오 공식 문서 기준 authorization_code로 고정
        params.add("client_id", kakaoClientId);
        params.add("client_secret", kakaoSecret);
        params.add("code", code);
        params.add("redirect_uri", kakaoRedirectUri);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        //카카오로부터 AccessToken 받아오기
        //restTemplate post방식으로 작동함
        ResponseEntity<String> accessTokenResponse = restTemplate.exchange(
                kakaoTokenUri, //카카오 엑세스토큰을 받아오는 uri, 카카오 공식 문서 기준 고정됨.
                HttpMethod.POST, //post방법으로
                kakaoTokenRequest,  //요청할 HttpEntity
                String.class); //String 타입으로 반환

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); //LocalDateTime타입의 객체를 직렬/역직렬
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  //기본?
        KakaoTokenDto kakaoTokenDto = null;  //카카오토큰을 받아올 객체

        try {
            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
            log.info("kakaoTokenDto = {}", kakaoTokenDto);
        } catch (JsonProcessingException e) {
            log.error("카카오 Token 받아오기 실패");
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
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",  //카카오공식문서기준 고정, 카카오유저정보 얻어오는 uri
                HttpMethod.POST, //post방법으로
                kakaoUserInfoRequest, //요청하는 HttpEntity
                String.class);  //String타입으로 반환

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
            log.info("jsonNode info={}", jsonNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String email = jsonNode.get("kakao_account").get("email").asText();
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
        User tokenUser = userRepository.findByEmail(kakaoEmail).orElseThrow(() ->
                new UserNotFoundException("토큰을 발급할 유저를 찾을 수 없습니다"));
        Long id = tokenUser.getId();
        AuthToken token = authTokenGenerator.generate(id);
        return new LoginResponseDto(id, nickname, kakaoEmail, token);
    }
}
