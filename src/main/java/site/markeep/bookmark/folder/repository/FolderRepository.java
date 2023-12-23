package site.markeep.bookmark.folder.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.markeep.bookmark.folder.dto.response.FolderListResponseDTO;
import site.markeep.bookmark.folder.dto.response.FolderResponseDTO;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.pinn.entity.Pin;
import site.markeep.bookmark.user.entity.User;

import java.util.List;
import java.util.Map;


public interface FolderRepository extends JpaRepository<Folder, Long> {

    // 특정 회원의 폴더 목록 요청
    @Query("SELECT f FROM Folder f WHERE f.user = :user ")
    List<Folder> findAllByUser(@Param("user") User user);

    @Query("SELECT count(p) as pincount FROM Pin p WHERE p.folder.id = :folderId")
    int countPinsByFolderId(@Param("folderId") Long folderId);

    @Query("SELECT f FROM Folder f LEFT JOIN f.pins p GROUP BY f ORDER BY COUNT(p) DESC, f.createDate DESC")
    Page<Folder> findAllOrderByPinCountDesc(Pageable pageable);

    @Query(value = "SELECT user_id FROM Folder WHERE folder_id = ?1", nativeQuery = true)
    Long getAllFolderInfo(Long folderId);




}
