package site.markeep.bookmark.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import site.markeep.bookmark.user.dto.request.PasswordUpdateRequestDTO;

import javax.persistence.EntityManager;

import static site.markeep.bookmark.user.entity.QUser.user;

//QueryDSL 사용하려고 만든 클래스
// 커스텀클래스를 구현하고 있다
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    private final BCryptPasswordEncoder encoder;

    public void updatePassword(PasswordUpdateRequestDTO dto){

        queryFactory
                .update(user)
                .set(user.password, encoder.encode(dto.getPassword()))
                .where(user.email.eq(dto.getEmail()))
                .execute();

        em.flush();
        em.clear();
    }

}
