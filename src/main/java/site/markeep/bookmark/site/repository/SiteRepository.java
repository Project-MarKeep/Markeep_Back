package site.markeep.bookmark.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.markeep.bookmark.site.entity.Site;

import java.util.List;
import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, Long> {
    List<Site> findByFolderId(Long folderId);

}
