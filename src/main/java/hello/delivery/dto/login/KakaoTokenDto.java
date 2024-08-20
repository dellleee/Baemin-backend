package hello.delivery.dto.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카카오로부터 받은 Response dto
 */
@Getter
@NoArgsConstructor //역직렬화를 위한 기본 생성자
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoTokenDto {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("id_token")
    private String idToken;
    @JsonProperty("expires_in")
    private String expiresIn;
    @JsonProperty("refresh_token_expires_in")
    private int refreshTokenExpiresIn;
    @JsonProperty("scope")
    private String scope;

    @Builder
    public KakaoTokenDto(String accessToken, String tokenType, String refreshToken, String idToken, String expiresIn, int refreshTokenExpiresIn, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.refreshToken = refreshToken;
        this.idToken = idToken;
        this.expiresIn = expiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
        this.scope = scope;
    }
}
