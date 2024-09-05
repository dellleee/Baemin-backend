package hello.delivery.service;

import hello.delivery.Exception.AddressNotFoundException;
import hello.delivery.Exception.UserNotFoundException;
import hello.delivery.entity.Address;
import hello.delivery.entity.User;
import hello.delivery.repository.AddressRepository;
import hello.delivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public Address save(Address address) {
        return addressRepository.save(address);
    }

    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    public Address findById(Long id) {
        return addressRepository.findById(id).orElseThrow(
                () -> new AddressNotFoundException("저장된 주소를 찾을 수 없습니다"));
    }

    /**
     * 주소 저장, 업데이트하기
     * @param id
     * @param address
     * @return
     */

    @Transactional
    public void updateAddress(Long id, Address address) {

        //회원조회
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("주소를 업데이트 할 회원을 찾을 수 없습니다"));
        //주소 지정
        findUser.addAddress(address);
    }

    /**
     * 과거 주소 이력 조회
     * @param id
     * @return
     */
    public List<Address> getAddressHistory(Long id) {
        //회원조회
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("주소이력을 가져욜 회원을 찾을 수 없습니다"));
        return findUser.getAddressHistory();
    }


}
