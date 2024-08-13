package hello.delivery.config.security.jwt;

import hello.delivery.config.security.auth.PrincipalDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Slf4j
public class JwtTokenProvider {

    private final int JWT_EXPIRATIONS_MS = 6000 * 1;
    private final int JWT_REFRESH_EXPIRATIONS_MS = 6000 * 10;
    private final Key key;
    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret, UserDetailsService userDetailsService) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userDetailsService = userDetailsService;
    }

    public String generateAccessToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATIONS_MS);

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        Claims claims = Jwts.claims().subject(String.valueOf(principalDetails.getUser().getId())).build();

        JwtBuilder builder = Jwts.builder()
                .claims(claims)
                .id(String.valueOf(principalDetails.getUser().getId()))
                .issuedAt(now)
                .subject(principalDetails.getUsername())
                .issuer("delivery.com")
                .signWith(key)
                .expiration(expiryDate);
        return builder.compact();
    }

    public String generateRefreshToken() {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_REFRESH_EXPIRATIONS_MS);

        return Jwts.builder()
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getUsernameByAccessToken(String accessToken) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(accessToken);
        return claims.getPayload().getSubject();
    }

    public boolean validateToken(String accessToken) {
        try {
            Claims payload = Jwts.parser().verifyWith((SecretKey) key)
                    .build().parseSignedClaims(accessToken)
                    .getPayload();
            log.info("claims expiration = {}", payload.getExpiration());
            return !payload.getExpiration().before(new Date()); //만료된 토큰이면 true 반환
        } catch (SignatureException e) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    public String getBearerTokenToString(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring("Bearer".length());
        }
        return null;
    }

    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("authorization", "Bearer" + accessToken);
    }

    public void refreshTokenInfoCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", "Bearer" + refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(3600 * 24); //24시간
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
    }

    public Authentication getAuthenticationByAccessToken(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsernameByAccessToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Authentication getAuthenticationByUsername(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
