package hello.delivery.controller;

import hello.delivery.dto.login.LoginResponseDto;
import hello.delivery.service.KakaoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ServerController {

    private final KakaoService kakaoService;

    @GetMapping("/login/oauth2/callback/kakao")
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestParam String code, HttpServletRequest request) {

        try {
            String currentDomain = request.getServerName();  //현재 도메인 확인(로컬인지 배포인지) , 배포하고 난 후
            return ResponseEntity.ok(kakaoService.kakaoLogin(code, currentDomain));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item Not Found");
        }

    }
}
