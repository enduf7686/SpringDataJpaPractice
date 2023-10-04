package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 사용자 정의 리포지토리 기능을 만들 때 주의 사항
 * 구현체의 이름을 원래 인터페이스 + Impl 로 하거나 (MemberRepository + Impl)
 * 커스텀 인터페이스의 이름 + Impl 로 해야한다. (MemberRepositoryCustom + Impl)
 * 둘 중 하나 선택
 */
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
