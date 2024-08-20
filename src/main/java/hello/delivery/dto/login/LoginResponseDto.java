package hello.delivery.dto.login;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class LoginResponseDto<T> {

    private Long id;
    private String nickname;
    private String email;
    private List<T> token;

    @Builder
    public LoginResponseDto(Long id, String nickname, String email, List<T> token) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.token = token;
    }
}
