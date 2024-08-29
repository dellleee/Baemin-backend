package hello.delivery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Order extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentMethod;
    private int totalPrice;
    private String request;
    private String status;

    @Builder
    public Order(String paymentMethod, int totalPrice, String request, String status) {
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.request = request;
        this.status = status;
    }
}
