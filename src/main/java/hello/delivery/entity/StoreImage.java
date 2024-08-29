package hello.delivery.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class StoreImage extends BaseTimeEntity {

    private String storePictureUrl;
    private String status;
}
