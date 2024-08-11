package hello.delivery.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "t_refresh_token")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @Column(name = "refresh_id", nullable = false)
    private Long id;
    @Column(name = "refresh_token",nullable = false,length = 200)
    private String refreshToken;

    @Builder
    public RefreshToken(Long id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
