package site.markeep.bookmark.folder.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.user.entity.User;

import java.util.List;



public interface FolderRepository extends JpaRepository<Folder, Long> , FolderRepositoryCustom {

    // 특정 회원의 폴더 목록 요청
    @Query("SELECT f FROM Folder f WHERE f.user = :user ")
    List<Folder> findAllByUser(@Param("user") User user);

    @Query("SELECT count(p) as pincount FROM Pin p WHERE p.folder.id = :folderId")
    int countPinsByFolderId(@Param("folderId") Long folderId);

//    Page<Folder> findAllOrderByPinCountkeywords(Pageable pageable, String[] keywords);

    @Query(value = "SELECT user_id FROM Folder WHERE folder_id = :folderId", nativeQuery = true)
    Long getFolderUser(@Param("folderId") Long folderId);

//    @Query("SELECT f FROM Folder f LEFT JOIN f.pins p " +
//            "WHERE (:keywords IS NULL OR LOWER(f.title) LIKE LOWER(CONCAT('%', :keywords[%d], '%'))) " +
//            "GROUP BY f ORDER BY COUNT(p) DESC, f.createDate DESC")
//        Page<Folder> findAllOrderByPinCountkeywords(Pageable pageable, @Param("keywords") String[] keywords);
//    Page<Folder> findAllOrderByPinCountkeywords(Pageable pageable, @Param("keywords") String keywords);

//    @Query("SELECT f FROM Folder f LEFT JOIN f.pins p GROUP BY f ORDER BY COUNT(p) DESC, f.createDate DESC")
//    Page<Folder> findAllOrderByPinCountDesc(Pageable pageable);

}
