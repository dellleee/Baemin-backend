package hello.delivery.repository.User;

import hello.delivery.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    public Optional<User> findById(Long id);
}
