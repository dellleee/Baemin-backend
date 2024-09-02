package hello.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roadAddress;  //주소
    private String addressDetail;  //상세주소
    @Column(length = 10)
    private String zipcode; //우편번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  //연관관계의 주인

    @Builder
    public Address(String roadAddress, String addressDetail, String zipcode) {
        this.roadAddress = roadAddress;
        this.addressDetail = addressDetail;
        this.zipcode = zipcode;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
