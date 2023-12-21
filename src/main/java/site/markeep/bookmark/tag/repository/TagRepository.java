package site.markeep.bookmark.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.markeep.bookmark.tag.entity.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Long> {
//    Optional<Tag> findById(Long id);

}
