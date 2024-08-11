package hello.delivery.controller.user;

import hello.delivery.config.security.auth.PrincipalDetails;
import hello.delivery.config.security.jwt.JwtTokenProvider;
import hello.delivery.dto.User.LoginDto;
import hello.delivery.dto.User.TokenDto;
import hello.delivery.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;

    @Operation(summary = "로그인",description = "로그인 메서드")
    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    public TokenDto signin(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @RequestBody LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        log.info("LOGIN SUCCESS >>> " + principalDetails.getUser().getUsername());

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        log.info("accessToken={}", accessToken);
        log.info("refreshToken={}", refreshToken);

        TokenDto jwtDto = TokenDto.builder().accessToken(accessToken).build();

        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.refreshTokenInfoCookie(response, refreshToken);

        return jwtDto;
    }

    @Operation(summary = "리프레시토큰 재발급",description = "refresh토큰 재발급 메서드")
    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public TokenDto refresh(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @CookieValue(value = "refreshToken") String bearerRefreshToken) {

        log.info("token = {}", bearerRefreshToken);
        TokenDto tokenDto = jwtService.refresh(bearerRefreshToken);

        jwtTokenProvider.setHeaderAccessToken(response, tokenDto.getAccessToken());
        jwtTokenProvider.refreshTokenInfoCookie(response, tokenDto.getRefreshToken());

        return tokenDto;
    }
}
