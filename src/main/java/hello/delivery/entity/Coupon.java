package hello.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Coupon extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;
    private String name;
    private String content;
    private int deductedPrice;
    private int minDeliveryPrice;
    private LocalDateTime expiredDate;
    private String status;

    @Builder
    public Coupon(String name, String content, int deductedPrice, int minDeliveryPrice, LocalDateTime expiredDate, String status) {
        this.name = name;
        this.content = content;
        this.deductedPrice = deductedPrice;
        this.minDeliveryPrice = minDeliveryPrice;
        this.expiredDate = expiredDate;
        this.status = status;
    }
}
