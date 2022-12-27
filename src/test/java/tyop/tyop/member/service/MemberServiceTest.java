package tyop.tyop.member.service;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import tyop.tyop.member.model.Member;
import tyop.tyop.member.model.Role;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private EntityManager em;

    @Transactional
    public Long makeMember() {
        Member member = Member.builder()
                .email("yc1234@gmail.com")
                .password("1234")
                .auth(Role.MEMBER)
                .build();
        em.persist(member);

        return member.getId();
    }

    @Test
    @Transactional
    void updatePasswordTest() {
        //given
        Long id = makeMember();
        String inputPassword = "1111";

        //when
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String newPassword =  passwordEncoder.encode(inputPassword);
        Member member = Member.builder()
                .id(id)
                .password(newPassword)
                .build();
        em.merge(member);

        Member findMember = em.find(Member.class, id);

        //then
        boolean matches = passwordEncoder.matches(inputPassword, findMember.getPassword());
        Assertions.assertThat(matches).isTrue();
    }

    @Test
    @Transactional
    void blockMemberTest() {
        //given
        Long id = makeMember();

        //when
        Member member = em.find(Member.class, id);
        Member updatedMember = Member.builder()
                .id(id)
                .auth(Role.BLOCK)
                .build();
        em.merge(updatedMember);

        //then
        Assertions.assertThat(em.find(Member.class, id).getAuth()).isEqualTo(Role.BLOCK);
    }
}