package hello.delivery.service;

import hello.delivery.Exception.login.TokenNotFoundException;
import hello.delivery.config.security.jwt.JwtTokenProvider;
import hello.delivery.dto.User.TokenDto;
import hello.delivery.entity.User.RefreshToken;
import hello.delivery.entity.User.User;
import hello.delivery.repository.User.RefreshTokenRepository;
import hello.delivery.repository.User.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JwtService {

    private final ApplicationContext context;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken);
    }

    public void saveRefreshToken(TokenDto tokenDto) {

        refreshTokenRepository.findById(tokenDto.getId())
                .ifPresentOrElse(
                        r -> {
                            r.setRefreshToken(tokenDto.getRefreshToken());
                        },
                        () ->{
                            RefreshToken token = RefreshToken.builder()
                                    .id(tokenDto.getId())
                                    .refreshToken(tokenDto.getRefreshToken())
                                    .build();
                            refreshTokenRepository.save(token);
                        });
    }

    public TokenDto refresh(String bearerRefreshToken) {
        JwtTokenProvider jwtTokenProvider = context.getBean(JwtTokenProvider.class);
        log.info("bearerrefreshtoken : {}", bearerRefreshToken);
        String refreshToken = jwtTokenProvider.getBearerTokenToString(bearerRefreshToken);
        log.info("refreshtoken : {} ", refreshToken);

        //유효성 체크
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new TokenNotFoundException("TokenValidateTestFailed");
        }

        RefreshToken findRefreshToken = this.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new TokenNotFoundException("refresh token was not found."));

        User user = userRepository.findById(findRefreshToken.getId()).orElseThrow(
                () -> new UsernameNotFoundException("user was not found")
        );

        Authentication authentication = jwtTokenProvider.getAuthenticationByUsername(user.getUsername());
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken();

        TokenDto tokenDto = TokenDto.builder()
                .id(findRefreshToken.getId())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        this.saveRefreshToken(tokenDto);
        return tokenDto;
    }
}
