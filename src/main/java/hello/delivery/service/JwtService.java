package hello.delivery.service;

import hello.delivery.Exception.TokenNotFoundException;
import hello.delivery.auth.jwt.JwtTokenProvider;
import hello.delivery.dto.login.TokenDto;
import hello.delivery.entity.RefreshToken;
import hello.delivery.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JwtService {

    private final ApplicationContext context;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;  //1시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 14일

    public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken);
    }

    /**
     *
     * @param id (user id)
     * @param bearerRefreshToken
     * @return
     */
    @Transactional
    public TokenDto refresh(Long id, String bearerRefreshToken) {

        JwtTokenProvider jwtTokenProvider = context.getBean(JwtTokenProvider.class);
        log.info("bearerRefreshToken : {}", bearerRefreshToken);
        String refreshToken = jwtTokenProvider.getBearerTokenToString(bearerRefreshToken);

        String idToString = String.valueOf(id);

        RefreshToken findRefreshToken = this.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new TokenNotFoundException("accessToken을 refresh할 refreshToken을 찾지 못했습니다"));

        //만료된 refreshToken이면 refresh,access 둘 다 재발급
        if (!jwtTokenProvider.validateToken(refreshToken)) {  //false여야만 돌아감

            //만료된 refreshToken 삭제
            refreshTokenRepository.delete(findRefreshToken);

                Date date = new Date();
                long now = date.getTime();
                Date accessExpireAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
                Date refreshExpireAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

                String newAccessToken = jwtTokenProvider.accessTokenGenerate(idToString, accessExpireAt);
                String newRefreshToken = jwtTokenProvider.refreshTokenGenerate(refreshExpireAt);

                //새로운 리프레시 토큰 저장
                RefreshToken token = RefreshToken.builder()
                        .refreshToken(newRefreshToken)
                        .build();

            RefreshToken savedToken = refreshTokenRepository.save(token);

            TokenDto tokenDto = TokenDto.builder()
                    .id(savedToken.getId())
                    .refreshToken(newRefreshToken)
                    .accessToken(newAccessToken)
                    .build();

            return tokenDto;

        } else {  //refresh토큰이 아직 유효하면 accessToken만 재발급

            long now = new Date().getTime();
            Date expiry = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
            String newAccessToken = jwtTokenProvider.accessTokenGenerate(idToString, expiry);

            TokenDto tokenDto = TokenDto.builder()
                    .id(findRefreshToken.getId())
                    .accessToken(newAccessToken)
                    .build();

            return tokenDto;
        }

    }


}
