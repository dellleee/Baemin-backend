package hello.delivery.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hello.delivery.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_name",length = 50,unique = true)
    private String username;
    @JsonIgnore
    @Column(name = "password", nullable = false, length = 200)
    private String password;
    private String nickname;
    private String role;

    @Builder
    public User(String username, String password, String nickname, String role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }
}
