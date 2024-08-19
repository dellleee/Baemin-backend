package hello.delivery.dto.login;

import hello.delivery.entity.Member;
import lombok.Data;

@Data
public class LoginResponseDto {

    public Boolean loginSuccess;
    public Member member;
}
