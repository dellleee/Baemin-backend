package hello.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Store extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;
    private String storeName;
    private int type;
    private String category;
    @Embedded
    private StoreImage storeImage;
    private String phone;
    private String content;
    private int minDeliveryPrice;
    private int deliveryTip;
    private int minDeliveryTime;
    private int maxDeliveryTime;
    private float rate;
    private String operationHour;
    private String closedDate;
    private String deliveryPossibleAddress;

    @Builder
    public Store(String storeName, int type, String category, StoreImage storeImage, String phone, String content, int minDeliveryPrice, int deliveryTip, int minDeliveryTime, int maxDeliveryTime, float rate, String operationHour, String closedDate, String deliveryPossibleAddress) {
        this.storeName = storeName;
        this.type = type;
        this.category = category;
        this.storeImage = storeImage;
        this.phone = phone;
        this.content = content;
        this.minDeliveryPrice = minDeliveryPrice;
        this.deliveryTip = deliveryTip;
        this.minDeliveryTime = minDeliveryTime;
        this.maxDeliveryTime = maxDeliveryTime;
        this.rate = rate;
        this.operationHour = operationHour;
        this.closedDate = closedDate;
        this.deliveryPossibleAddress = deliveryPossibleAddress;
    }
}
