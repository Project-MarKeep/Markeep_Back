package site.markeep.bookmark.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.markeep.bookmark.tag.entity.Tag;

public interface TagRepository extends JpaRepository<Tag,Long> {
//    Optional<Tag> findById(Long id);

//    @Modifying
//    @Query(value =  "DELETE FROM Tag WHERE folder.id = :folderId",nativeQuery = true)
//    int deleteTagsByFolderId(@Param("folderId") Long folderId);

    @Modifying
    @Query("DELETE FROM Tag t WHERE t.folder.id = :folderId")
    int deleteTagsByFolderId(@Param("folderId") Long folderId);


}
