package site.markeep.bookmark.pinn.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import site.markeep.bookmark.pinn.entity.Pin;

import java.util.List;

public interface PinRepository  extends JpaRepository<Pin, Long> {

    @Value("SELECT p FROM Pin p WHERE p.folder_id = :folderId")
    List<Pin> findAllByFolderId(Long folderId);
}
