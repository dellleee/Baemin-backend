package hello.delivery.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthToken {

    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long expiresIn;

    @Builder
    public AuthToken(String accessToken, String refreshToken, String grantType, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
        this.expiresIn = expiresIn;
    }

}
