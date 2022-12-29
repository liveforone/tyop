package tyop.tyop.member.service;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import tyop.tyop.member.dto.MemberRequest;
import tyop.tyop.member.model.Member;
import tyop.tyop.member.model.Role;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberService memberService;

    public void createMember(String email, String password) {
        MemberRequest dto = new MemberRequest();
        dto.setEmail(email);
        dto.setPassword(password);

        memberService.signup(dto);
    }

    @Test
    void signupTest() {
        //given
        String email = "yc1234@gmail.com";
        String password = "1234";

        //when
        createMember(email, password);

        //then
        Member member = memberService.getMemberEntity(email);
        Assertions.assertThat(member.getEmail()).isEqualTo(email);
    }

    @Test
    void blockMemberTest() {
        //given
        String email = "yc1234@gmail.com";
        String password = "1234";
        createMember(email, password);

        //when
        memberService.blockMember(email);
        em.flush();
        em.clear();

        //then
        Assertions
                .assertThat(memberService.getMemberEntity(email).getAuth())
                .isEqualTo(Role.BLOCK);
    }

    @Test
    void updatePasswordTest() {
        //given
        String email = "yc1234@gmail.com";
        String password = "1234";
        createMember(email, password);

        //when
        Member member = memberService.getMemberEntity(email);
        String newPassword = "9999";
        memberService.updatePassword(member.getId(), newPassword);
        em.flush();
        em.clear();

        //then
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(newPassword, memberService.getMemberEntity(email).getPassword());
        Assertions.assertThat(matches).isTrue();
    }
}