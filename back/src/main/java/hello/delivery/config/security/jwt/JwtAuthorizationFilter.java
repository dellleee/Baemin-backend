package hello.delivery.config.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("{} -> successfulAuthentication -> 인증이나 권한이 필요한 주소요청이 됨.", this.getClass());
        String bearerAccessToken = request.getHeader("authorization");
        log.info("BearerAccessToken = {}",bearerAccessToken);
        String accessToken = jwtTokenProvider.getBearerTokenToString(bearerAccessToken);
        log.info("accessToken = {}", accessToken);

        if (StringUtils.hasText(accessToken) && jwtTokenProvider.validateToken(accessToken)) {
            Authentication authentication = jwtTokenProvider.getAuthenticationByAccessToken(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security Context에 '{}' 인증정보를 저장했습니다.", authentication.getName());
        } else {
            log.debug("유효한 JWT토큰이 없습니다");
        }
        filterChain.doFilter(request, response);
    }


}
