package site.markeep.bookmark.folder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.markeep.bookmark.folder.entity.Folder;

import java.util.Optional;


public interface FolderRepository extends JpaRepository<Folder,Long> {
    Optional<Folder> findById(Long id);
}
