package hello.delivery.controller;

import hello.delivery.Exception.TokenNotFoundException;
import hello.delivery.dto.login.TokenDto;
import hello.delivery.dto.user.AddressRequestDto;
import hello.delivery.dto.user.AddressResponseDto;
import hello.delivery.entity.Address;
import hello.delivery.service.AddressService;
import hello.delivery.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    private final JwtService jwtService;
    private final AddressService addressService;


    @Operation(summary = "유저주소 저장하기", description = "유저의 현재 주소를 저장한다")
    @PostMapping("/{id}/address/save")
    public ResponseEntity<?> saveAddress(@PathVariable Long id,
                                         @Validated @RequestBody AddressRequestDto addressDto,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {  //유효성 검증 로직
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(bindingResult.getAllErrors());
        }
        Address address = Address.builder()
                .roadAddress(addressDto.getRoadAddress())
                .addressDetail(addressDto.getAddressDetail())
                .zipcode(addressDto.getZipcode())
                .build();

        Address savedAddress = addressService.save(address);
        addressService.updateAddress(id, savedAddress);


        return ResponseEntity.ok(AddressResponseDto.builder()
                .id(savedAddress.getId())
                .build());
    }

    @Operation(summary = "주소가져오기", description = "유저 주소기록 가져오기, id = 유저Id")
    @GetMapping("/{id}/address")
    public ResponseEntity<List> getAddress(@PathVariable Long id) {

        List<Address> history = addressService.getAddressHistory(id);
        List<AddressResponseDto> collect = history.stream()
                .map(AddressResponseDto::new)
                .collect(toList());

        return ResponseEntity.ok(collect);
    }

    @Operation(summary = "토큰 재발급", description = "리프레시토큰 만료되면 둘 다 재발급, 아니면 액세스만 재발급, 앞에 BEARER 꼭 포함해서 보내주세요")
    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> refresh(Long id, String bearerRefreshToken) {
        try {
            return ResponseEntity.ok(jwtService.refresh(id, bearerRefreshToken));
        } catch (TokenNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token Not Found");
        }

    }
}
