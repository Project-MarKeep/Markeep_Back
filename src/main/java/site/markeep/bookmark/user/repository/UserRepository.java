package site.markeep.bookmark.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.markeep.bookmark.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>,
UserRepositoryCustom{


    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.profileImage = :profileImage WHERE u.id = :userId")
    int modifyProfileImage(@Param("profileImage") String profileImage , @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User u SET u.nickname = :nickName WHERE u.id = :userId")
    int modifyNickname(@Param("nickName")String nickName,@Param("userId") Long id);
}
