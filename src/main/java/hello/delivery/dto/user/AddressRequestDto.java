package hello.delivery.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class AddressRequestDto {
    @NotBlank
    private String roadAddress;
    @NotBlank
    private String addressDetail;
    @NotBlank
    private String zipcode;

    @Builder
    public AddressRequestDto(String roadAddress, String addressDetail, String zipcode) {
        this.roadAddress = roadAddress;
        this.addressDetail = addressDetail;
        this.zipcode = zipcode;
    }
}
