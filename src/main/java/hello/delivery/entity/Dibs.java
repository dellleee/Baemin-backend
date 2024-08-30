package hello.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Dibs extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "dibs_id")
    private Long id;
    private String status;
}
