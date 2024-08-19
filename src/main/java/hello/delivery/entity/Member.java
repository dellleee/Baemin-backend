package hello.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String userName;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(nullable = false, length = 20, unique = true)
    private String email;

    @Column(nullable = false, length = 10)
    private String userGrade;

    @Column(nullable = false, length = 10)
    private String status;

    @Embedded
    private Address address;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private  Role role;

    @Builder
    public Member(String userName, String password, String email, String userGrade, String status, Address address, Role role) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.userGrade = userGrade;
        this.status = status;
        this.address = address;
        this.role = role;
    }
}
