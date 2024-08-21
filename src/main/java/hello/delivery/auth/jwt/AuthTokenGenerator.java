package hello.delivery.auth.jwt;

import hello.delivery.entity.AuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthTokenGenerator {

    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;  //1시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 14일

    private final JwtTokenProvider jwtTokenProvider;

    public AuthToken generate(Long id) {
        Date date = new Date();
        long now = date.getTime();
        Date accessExpireAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshExpireAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        String subject = String.valueOf(id);
        String accessToken = jwtTokenProvider.accessTokenGenerate(subject, accessExpireAt);
        String refreshToken = jwtTokenProvider.refreshTokenGenerate(refreshExpireAt);

        return AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(BEARER_TYPE)
                .expiresIn(ACCESS_TOKEN_EXPIRE_TIME)
                .build();
    }
}
