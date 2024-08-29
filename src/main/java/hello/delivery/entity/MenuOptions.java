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
public class MenuOptions extends BaseTimeEntity{  //엔티티인가 값타입인가?

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String option;
    private String content;
    private int price;
    private String status;

    @Builder
    public MenuOptions(String option, String content, int price, String status) {
        this.option = option;
        this.content = content;
        this.price = price;
        this.status = status;
    }
}
