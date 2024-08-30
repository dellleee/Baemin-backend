package hello.delivery.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class StoreImage {

    private String storePictureUrl;
    private String status;
}
