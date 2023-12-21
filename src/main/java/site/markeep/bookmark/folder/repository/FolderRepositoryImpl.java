package site.markeep.bookmark.folder.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.folder.entity.QFolder;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
@Slf4j

public class FolderRepositoryImpl implements FolderRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public FolderRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Folder> findAllOrderByPinCountKeyWords(Pageable pageable, String[] keywords) {
        QFolder folder = QFolder.folder;

        BooleanExpression predicate = Arrays.stream(keywords)
                .filter(keyword -> keyword != null && !keyword.isEmpty())
                .map(keyword -> folder.title.lower().like("%" + keyword.toLowerCase() + "%"))
                .reduce(BooleanExpression::and)
                .orElse(null);


        List<Folder> content = queryFactory.selectFrom(folder)
                .leftJoin(folder.pins)
                .where(predicate)
                .groupBy(folder)
                .orderBy(folder.pins.size().desc(), folder.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(folder)
                .leftJoin(folder.pins)
                .where(predicate)
                .groupBy(folder)
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

}