package site.markeep.bookmark.pinn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.markeep.bookmark.pinn.entity.Pin;

public interface PinRepository  extends JpaRepository<Pin, Long> {
}
