package hello.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Menu extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;
    private String category;
    private String menuName;
    private int price;
    private String menuPictureUrl;
    private int popularity;
    private String status;

    @Builder
    public Menu(String category, String menuName, int price, String menuPictureUrl, int popularity, String status) {
        this.category = category;
        this.menuName = menuName;
        this.price = price;
        this.menuPictureUrl = menuPictureUrl;
        this.popularity = popularity;
        this.status = status;
    }
}
