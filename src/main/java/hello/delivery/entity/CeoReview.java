package hello.delivery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CeoReview extends BaseTimeEntity {

    @Id
    private Long id;  //리뷰와 같은 pk번호를 쓴다
    private String content;
    private String status;

    @Builder
    public CeoReview(Long id, String content, String status) {
        this.id = id;
        this.content = content;
        this.status = status;
    }
}
