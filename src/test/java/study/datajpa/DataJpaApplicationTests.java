package study.datajpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import study.datajpa.repository.MemberRepository;

@SpringBootTest
class DataJpaApplicationTests {

	@Autowired
	ApplicationContext ac;

	@Test
	void contextLoads() {

		System.out.println(ac.getBean("memberRepository", MemberRepository.class));
		System.out.println(ac.getBean("memberRepository", MemberRepository.class).getClass());
	}
}
