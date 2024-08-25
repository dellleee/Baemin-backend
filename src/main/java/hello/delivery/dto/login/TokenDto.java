package hello.delivery.dto.login;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenDto {

    private Long id;
    private String accessToken;
    private String refreshToken;

    @Builder
    public TokenDto(Long id, String accessToken, String refreshToken) {
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
