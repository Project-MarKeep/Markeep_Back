package site.markeep.bookmark.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.markeep.bookmark.auth.NewRefreshToken;

import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<NewRefreshToken, Long> {

    // 아직 메서드 구현 안함 -> 횟수 제한 용도 메서드 구현 예정
    Optional<NewRefreshToken> findByReissueCountAndUserId(Long id, Long count);

}
