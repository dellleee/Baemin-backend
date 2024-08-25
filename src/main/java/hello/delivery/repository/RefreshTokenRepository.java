package hello.delivery.repository;

import hello.delivery.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    public Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
