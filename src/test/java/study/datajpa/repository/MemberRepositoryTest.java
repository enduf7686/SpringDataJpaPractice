package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamJpaRepository;

    @Test
    void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).orElse(null);
        assertThat(savedMember.getId()).isEqualTo(findMember.getId());
        assertThat(savedMember.getUsername()).isEqualTo(findMember.getUsername());
        assertThat(savedMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).orElse(null);
        Member findMember2 = memberRepository.findById(member2.getId()).orElse(null);
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2l);

        memberRepository.delete(member1);
        memberRepository.delete(member2);
        assertThat(memberRepository.count()).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMembers = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(findMembers.size()).isEqualTo(1);
        assertThat(findMembers).contains(m2);
    }

    @Test
    void findMembers() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMembers = memberRepository.findMembers("AAA", 15);
        assertThat(findMembers.size()).isEqualTo(1);
        assertThat(findMembers).contains(m2);
    }

    @Test
    void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> UsernameList = memberRepository.findUsernameList();
        assertThat(UsernameList.size()).isEqualTo(2);
        assertThat(UsernameList).contains(m1.getUsername());
        assertThat(UsernameList).contains(m2.getUsername());
        System.out.println("UsernameList = " + UsernameList);
    }

    @Test
    void findMemberDto() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamJpaRepository.save(teamA);
        teamJpaRepository.save(teamB);

        Member m1 = new Member("AAA", 10, teamA);
        Member m2 = new Member("BBB", 20, teamB);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<MemberDto> memberDtoList = memberRepository.findMemberDto();
        System.out.println("memberDtoList = " + memberDtoList);
    }
    
    @Test
    void findByUsername() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Member findMember = memberRepository.findByUsername("AAA").orElse(null);
        System.out.println("findMember = " + findMember);
    }

    @Test
    void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));
        memberRepository.save(new Member("member6", 60));

        int age = 45;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAgeLessThan(age, pageRequest);

        Page<MemberDto> dtoPage = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null)); //엔티티를 DTO로 바꾸는 기능

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3); //현재 페이지의 사이즈
        assertThat(page.getTotalElements()).isEqualTo(4); //토탈 사이즈
        assertThat(page.getNumber()).isEqualTo(0); //현재 페이지
        assertThat(page.getTotalPages()).isEqualTo(2); //총 페이지 수
        assertThat(page.isFirst()).isTrue(); //현재 페이지가 첫번째 페이지인지 확인
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는지 확인
        assertThat(page.isLast()).isFalse(); //현재 페이지가 마지막 페이지인지 확인
    }

    @Test
    void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));
        memberRepository.save(new Member("member6", 60));

        int resultCount = memberRepository.bulkAgePlus(70);

        assertThat(resultCount).isEqualTo(6);
    }

    @Test
    void callCustom() {
//        List<Member> memberCustom = memberRepository.findMemberCustom();
    }

    @Test
    void projections() {
        Team teamA = new Team("teamA");
        teamJpaRepository.save(teamA);

        Member m1 = new Member("AAA", 10, teamA);
        Member m2 = new Member("BBB", 20, teamA);
        memberRepository.save(m1);
        memberRepository.save(m2);


    }
}