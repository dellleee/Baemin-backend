package hello.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addressHistory = new ArrayList<>();

    @Builder
    public User(String userName, String email, String loginType, String userGrade, String status, Role role) {
        this.userName = userName;
        this.email = email;
        this.loginType = loginType;
        this.userGrade = userGrade;
        this.status = status;
        this.role = role;
    }

    // == 연관관계 메서드 == //
    public void addAddress(Address address) {
        addressHistory.add(address);
        address.setUser(this);
    }

}
