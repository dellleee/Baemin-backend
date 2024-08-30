package hello.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MenuDetail extends BaseTimeEntity {  //엔티티인가 값타입인가?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_detail_id")
    private Long id;
    private String detail;
    private String content;
    private int price;
    private String status;

    @Builder
    public MenuDetail(String detail, String content, int price, String status) {
        this.detail = detail;
        this.content = content;
        this.price = price;
        this.status = status;
    }
}
