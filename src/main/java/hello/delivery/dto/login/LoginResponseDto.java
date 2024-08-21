package hello.delivery.dto.login;

import hello.delivery.entity.AuthToken;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class LoginResponseDto {

    private Long id;
    private String nickname;
    private String email;
    private AuthToken token;

    @Builder
    public LoginResponseDto(Long id, String nickname, String email, AuthToken token) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.token = token;
    }
}
