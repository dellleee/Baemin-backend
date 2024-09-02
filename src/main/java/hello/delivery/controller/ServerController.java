package hello.delivery.controller;

import hello.delivery.dto.login.LoginResponseDto;
import hello.delivery.service.GoogleService;
import hello.delivery.service.KakaoService;
import hello.delivery.service.NaverService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/login/oauth2/callback")
@CrossOrigin
public class ServerController {

    private final KakaoService kakaoService;
    private final NaverService naverService;
    private final GoogleService googleService;

    @Operation(summary = "로그인", description = "카카오 로그인")
    @GetMapping("/kakao")
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestParam String code, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(kakaoService.kakaoLogin(code));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item Not Found");
        }

    }

    @Operation(summary = "로그인", description = "네이버 로그인")
    @GetMapping("/naver")
    public ResponseEntity<LoginResponseDto> naverLogin(@RequestParam String code, @RequestParam String state, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(naverService.naverLogin(code, state));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item Not Found");
        }
    }

    @Operation(summary = "로그인", description = "구글 로그인")
    @GetMapping("/google")
    public ResponseEntity<LoginResponseDto> googleLogin(@RequestParam String code, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(googleService.googleLogin(code));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item Not Found");
        }
    }
}
