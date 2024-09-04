package hello.delivery.service;

import hello.delivery.entity.Address;
import hello.delivery.entity.Role;
import hello.delivery.entity.User;
import hello.delivery.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
@SpringBootTest
@Transactional
class AddressServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressService addressService;

    @Test
    void findById() {
        //given
        User user = User.builder()
                .email("eeee")
                .status("일반")
                .role(Role.USER)
                .userGrade("고마운 분")
                .loginType("테스트")
                .userName("닉네임")
                .build();

        Address address = Address.builder()
                .roadAddress("서울")
                .addressDetail("detail")
                .zipcode("11111")
                .build();

        //when
        userRepository.save(user);
        Long id = user.getId();
        Address savedAddress = addressService.save(address);
        Long addressId = savedAddress.getId();

        Optional<User> byId = userRepository.findById(id);
        User findUser = byId.get();
        Address findAddress = addressService.findById(addressId);

        //then
        assertThat(findUser.getUserName()).isEqualTo(user.getUserName());
        assertThat(findAddress.getZipcode()).isEqualTo(address.getZipcode());

    }

    @Test
    void findAll() {
        //given
        User user = User.builder()
                .email("eeee")
                .status("일반")
                .role(Role.USER)
                .userGrade("고마운 분")
                .loginType("테스트")
                .userName("닉네임")
                .build();
        Address address = Address.builder()
                .roadAddress("서울")
                .addressDetail("detail")
                .zipcode("11111")
                .build();
        Address address2 = Address.builder()
                .roadAddress("서울")
                .addressDetail("detail")
                .zipcode("22222")
                .build();

        //when
        userRepository.save(user);
        addressService.save(address);
        addressService.save(address2);

        List<User> all = userRepository.findAll();
        List<Address> addressList = addressService.findAll();

        //then
        assertThat(all.size()).isEqualTo(1);
        assertThat(addressList.size()).isEqualTo(2);
    }

    @Test
    void updateAddress() {
        //given
        User user = User.builder()
                .email("eeee")
                .status("일반")
                .role(Role.USER)
                .userGrade("고마운 분")
                .loginType("테스트")
                .userName("닉네임")
                .build();
        User savedUser = userRepository.save(user);
        Long id = savedUser.getId();  //저장

        Address address = Address.builder()
                .roadAddress("서울")
                .addressDetail("detail")
                .zipcode("11111")
                .build();
        Address address2 = Address.builder()
                .roadAddress("서울")
                .addressDetail("detail")
                .zipcode("22222")
                .build();
        Address savedAddress = addressService.save(address);
        Address savedAddress2 = addressService.save(address2);

        //when
        addressService.updateAddress(id, savedAddress);
        addressService.updateAddress(id, savedAddress2);

        User findUser = userRepository.findById(id).get();

        //then
        assertThat(findUser.getAddressHistory()).contains(savedAddress,savedAddress2);
        assertThat(addressService.getAddressHistory(id)).contains(savedAddress,savedAddress2);
        assertThat(savedAddress.getUser()).isEqualTo(findUser);
        assertThat(savedAddress2.getUser()).isEqualTo(findUser);
    }

}