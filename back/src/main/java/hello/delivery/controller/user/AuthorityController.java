package hello.delivery.controller.user;

import hello.delivery.config.security.auth.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthorityController {

    @Operation(summary = "User 메인페이지",description = "user 권한을 가지고 있는 회원페이지")
    @GetMapping("/user/main")
    public String userMain(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        log.info("authentication : " + principalDetails.getUsername());
        return "user-main";
    }

    @Operation(summary = "Manager 메인페이지",description = "manager 권한을 가지고 있는 회원페이지")
    @GetMapping("/manager/main")
    public String managerMain(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        log.info("authentication : " + principalDetails.getUsername());
        return "manager - main";
    }
}
