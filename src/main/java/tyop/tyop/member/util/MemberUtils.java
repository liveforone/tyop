package tyop.tyop.member.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tyop.tyop.jwt.JwtAuthenticationFilter;
import tyop.tyop.member.model.Member;
import tyop.tyop.utility.CommonUtils;

import java.net.URI;

public class MemberUtils {

    /*
     * 이메일 중복 검증
     * 반환 값 : false(중복X), true(중복)
     */
    public static boolean isDuplicateEmail(Member member) {

        return !CommonUtils.isNull(member);
    }

    /*
     * 비밀번호 같지 않은지 검증
     * 반환 값 : true(같지 않을때), false(같을때)
     */
    public static boolean isNotMatchingPassword(String inputPassword, String originalPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return !encoder.matches(inputPassword, originalPassword);
    }

    public static String makeRandomNickname() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static boolean isDuplicateNickname(Member member) {
        return !CommonUtils.isNull(member);
    }

    public static HttpHeaders makeHttpHeadersWhenSignupRedirect(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();

        String url = "/member/login";
        String token = JwtAuthenticationFilter.resolveToken(request);

        httpHeaders.setBearerAuth(token);
        httpHeaders.setLocation(URI.create(url));

        return httpHeaders;
    }
}
