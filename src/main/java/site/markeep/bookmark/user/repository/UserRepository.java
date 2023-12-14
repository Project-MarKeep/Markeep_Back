package site.markeep.bookmark.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.markeep.bookmark.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>,
UserRepositoryCustom{


    Optional<User> findByEmail(String email);









}
