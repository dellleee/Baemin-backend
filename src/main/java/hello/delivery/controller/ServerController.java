package hello.delivery.controller;

import hello.delivery.dto.login.LoginResponseDto;
import hello.delivery.service.KakaoService;
import hello.delivery.service.NaverService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;


@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class ServerController {

    private final KakaoService kakaoService;
    private final NaverService naverService;

    @Operation(summary = "로그인", description = "카카오 로그인")
    @GetMapping("/login/oauth2/callback/kakao")
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestParam String code, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(kakaoService.kakaoLogin(code));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item Not Found");
        }

    }

    @Operation(summary = "로그인", description = "네이버 로그인")
    @GetMapping("/login/oauth2/callback/naver")
    public ResponseEntity<LoginResponseDto> naverLogin(@RequestParam String code,@RequestParam String state, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(naverService.naverLogin(code,state));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item Not Found");
        }
    }
}
