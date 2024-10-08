package hello.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;
    private int rating;
    private String content;
    private String reviewPictureUrl;
    private String status;

    @Builder
    public Review(int rating, String content, String reviewPictureUrl, String status) {
        this.rating = rating;
        this.content = content;
        this.reviewPictureUrl = reviewPictureUrl;
        this.status = status;
    }
}
