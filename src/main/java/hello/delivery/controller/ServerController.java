package hello.delivery.controller;

import hello.delivery.dto.login.LoginResponseDto;
import hello.delivery.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ServerController {

    private final AuthService authService;

    @GetMapping("/login/oauth2/callback/kakao")
    public ResponseEntity<LoginResponseDto> kakaoLogin(HttpServletRequest request) {
        String code = request.getParameter("code");  //인가코드 받음
        String kakaoAccessToken = String.valueOf(authService.getKakaoAccessToken(code));  //인가코드로 액세스토큰 발급받음
        return (ResponseEntity<LoginResponseDto>) ResponseEntity.ok();
    }
}
