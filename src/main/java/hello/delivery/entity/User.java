package hello.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String userName;


    @Column(nullable = false, length = 20, unique = true)
    private String email;

    private String loginType;

    @Column(nullable = false, length = 10)
    private String userGrade;

    @Column(nullable = false, length = 10)
    private String status;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private  Role role;

    @Builder
    public User(String userName, String email, String userGrade, String status, Role role, String loginType) {
        this.userName = userName;
        this.email = email;
        this.userGrade = userGrade;
        this.status = status;
        this.role = role;
        this.loginType = loginType;
    }
}
