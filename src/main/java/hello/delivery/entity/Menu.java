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
public class Menu extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
