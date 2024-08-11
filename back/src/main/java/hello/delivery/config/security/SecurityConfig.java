package hello.delivery.config.security;

import hello.delivery.config.security.auth.PrincipalDetailsService;
import hello.delivery.config.security.jwt.JwtAuthenticationFilter;
import hello.delivery.config.security.jwt.JwtAuthorizationFilter;
import hello.delivery.config.security.jwt.JwtTokenProvider;
import hello.delivery.service.JwtService;
import hello.delivery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CorsConfig corsConfig;

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(secret, userDetailsService());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new PrincipalDetailsService(userService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request ->
                        request.requestMatchers("api/user/**").hasAnyRole("USER", "MANAGER")
                                .requestMatchers("api/manager/**").hasRole("MANAGER")
                                .anyRequest().permitAll());

        http.addFilterBefore(corsConfig.corsFilter(), UsernamePasswordAuthenticationFilter.class);

        //login 주소가 호출되면 인증 및 토큰 발행 필터 추가
        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), jwtTokenProvider(), jwtService),
                UsernamePasswordAuthenticationFilter.class);

        //jwt 토큰 검사
        http.addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider()),
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
