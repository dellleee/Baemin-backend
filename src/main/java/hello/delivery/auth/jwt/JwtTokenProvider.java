package hello.delivery.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
}
