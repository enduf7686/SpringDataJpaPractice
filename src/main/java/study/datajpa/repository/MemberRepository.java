package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.username = :username and m.age > :age")
    List<Member> findMembers(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    Optional<Member> findByUsername(String username);

    Page<Member> findByAgeLessThan(int age, Pageable pageable);

    /**
     * 레프트 조인 같은 경우 카운트를 할 때 Member의 수만 세면 되기 때문에 join을 할 필요가 없다.
     * 하지만 스프링 데이터 JPA가 기본적으로 짜주는 count쿼리는 기본 쿼리와 같은 방식으로 진행되기 때문에 성능이 저하될 수 있다. (ex. join이 필요 없는데 join을 하는 경우)
     * 그래서 밑의 메서드와 같이 페이징 쿼리와 카운트 쿼리를 분리해서 구현할 수 있는 기능도 제공한다.
     * Sorting이 너무 복잡하면 PageRequest에 Sort를 넣지 말고 JPQL쿼리에 정렬 조건을 넣는 것이 더 낫다.
     */
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    /**
     * 조회가 아닌 벌크 연산을 할 때에는 @Modifying을 붙여줘야 한다.
     * clearAutomatically 옵션을 true로 설정하면 벌크 연산을 진행한 후에 영속성 컨텍스트를 자동으로 비워준다.
     */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 10 where m.age < :age")
    int bulkAgePlus(@Param("age") int age);

    /**
     @EntityGraph를 활용하면 내부적으로 fetch join을 이용해 지연 로딩으로 설정된 연관 객체들을 한 번에 불러온다.
     */
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    /**
     * 이름 기반 쿼리 메서드에도 @EntityGraph 적용 가능
     * @Query 메서드에도 적용 가능
     * 다중 페치 조인 등 복잡한 경우에는 그냥 JPQL에 풀어서 쓰는 게 낫다.
     */
    @EntityGraph(attributePaths = ("team"))
    @Query("select m from Member m where m.age = :age")
    Optional<Member> findMembers(@Param("age") int age);
}
