package hello.delivery.config.security.auth;

import hello.delivery.Exception.login.UserNotFoundException;
import hello.delivery.entity.User.User;
import hello.delivery.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService{

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("############################## PrincipalDetailsService - username : {}", username);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
        return new PrincipalDetails(user);
    }
}
