package hello.delivery.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.delivery.config.security.auth.PrincipalDetails;
import hello.delivery.dto.User.LoginDto;
import hello.delivery.dto.User.TokenDto;
import hello.delivery.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("{} - attempAuthentication -> 로그인 시도중", this.getClass());

        ObjectMapper om = new ObjectMapper();

        try {
            LoginDto userDto = om.readValue(request.getInputStream(), LoginDto.class);

            log.info("user.getUsername() : {}", userDto.getUsername());
            log.info("user.getPassword() : {}", userDto.getPassword());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDto.getUsername(), userDto.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            log.info(principalDetails.getUser().getUsername());
            return authentication;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.info("{} - successfulAuthentication ->인증완료", this.getClass());

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(authResult);
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        Long id = principalDetails.getUser().getId();

        TokenDto tokenDto = TokenDto.builder().id(id)
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
        jwtService.saveRefreshToken(tokenDto);

        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.refreshTokenInfoCookie(response, refreshToken);
    }
}
