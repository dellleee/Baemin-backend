package hello.delivery.repository.User;

import hello.delivery.entity.User.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    public Optional<RefreshToken> findById(Long id);

    public Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
