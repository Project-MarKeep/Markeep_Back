package site.markeep.bookmark.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.markeep.bookmark.site.entity.Site;

public interface SiteRepository extends JpaRepository<Site, Long> {
}
