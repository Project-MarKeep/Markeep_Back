package site.markeep.bookmark.folder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.user.entity.User;

import java.util.List;
import java.util.Optional;


public interface FolderRepository extends JpaRepository<Folder,Long> {
//    Optional<Folder> findById(Long id);

    // 특정 회원의 폴더 목록 요청
    @Query("SELECT f FROM Folder f WHERE f.user = :user ")
    List<Folder> findAllByUser(@Param("user") User user);
}
