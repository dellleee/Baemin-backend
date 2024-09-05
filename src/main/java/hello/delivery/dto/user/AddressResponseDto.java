package hello.delivery.dto.user;

import hello.delivery.entity.Address;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class AddressResponseDto {

    private Long id;
    private String roadAddress;
    private String addressDetail;
    private String zipcode;

    @Builder
    public AddressResponseDto(Long id, String roadAddress, String addressDetail, String zipcode) {
        this.id = id;
        this.roadAddress = roadAddress;
        this.addressDetail = addressDetail;
        this.zipcode = zipcode;
    }

    public AddressResponseDto(Address address) {
        this.id = address.getId();
        this.addressDetail = address.getAddressDetail();
        this.roadAddress = address.getRoadAddress();
        this.zipcode = address.getZipcode();
    }
}
