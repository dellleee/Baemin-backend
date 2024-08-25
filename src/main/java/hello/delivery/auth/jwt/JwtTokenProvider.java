package hello.delivery.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String accessTokenGenerate(String id, Date expiredAt) {
        return Jwts.builder()
                .subject(id)
                .expiration(expiredAt)
                .signWith(key)
                .compact();
    }

    public String refreshTokenGenerate(Date expiredAt) {
        return Jwts.builder()
                .expiration(expiredAt)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String refreshToken) {
        try {
            Claims payload = Jwts.parser().verifyWith((SecretKey) key)
                    .build().parseSignedClaims(refreshToken)
                    .getPayload();
            log.info("claims experation = {} ", payload.getExpiration());
            return !payload.getExpiration().before(new Date());  //유효한 리프레시 토큰이면 true를 보냄
        } catch (SignatureException e) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token");
            e.printStackTrace();
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token");
            e.printStackTrace();
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty");
            e.printStackTrace();
        }
        return false;  //만료된 리프레시 토큰이면 false를 보냄
    }

    //bearer 빼고, 순수 토큰 변환
    public String getBearerTokenToString(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            log.info("getBearerTokenToString = {} ",bearerToken.substring("Bearer ".length()));
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }

    //엑세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Authorization", "Bearer " + accessToken);
    }

    //리프레시토큰 쿠키 설정
    public void refreshTokenInfoCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", "Bearer " + refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(3600 * 24); //24시간
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
    }
}
