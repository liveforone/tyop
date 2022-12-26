package tyop.tyop.member.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class MemberUtilsTest {

    @Test
    void isNotMatchingPassword() {
        //given
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //when
        String inputPassword = "1111";
        String originalPassword = "1234";
        String encodeOriginalPassword = passwordEncoder.encode(originalPassword);

        //then
        boolean matches = passwordEncoder.matches(inputPassword, encodeOriginalPassword);
        Assertions.assertThat(matches).isFalse();
    }
}