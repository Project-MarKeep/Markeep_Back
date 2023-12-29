package site.markeep.bookmark.folder.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.markeep.bookmark.folder.entity.Folder;

import java.util.List;

public interface FolderRepositoryCustom {
    Page<Folder> findAllOrderByPinCountKeyWords(Pageable pageable, String[] keywords);

    Page<Folder> findAllByKeywords(Pageable pageable, Long userId, String[] keywords);
}

