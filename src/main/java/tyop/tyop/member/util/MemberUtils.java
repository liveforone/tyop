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

    private static final int BLOCK_COUNT = 10;

    public static boolean checkBlockCount(Member member) {
        return member.getBlockCount() >= BLOCK_COUNT;
    }

    public static boolean isDuplicateEmail(Member member) {

        return !CommonUtils.isNull(member);
    }

    public static boolean isDuplicateNickname(Member member) {
        return !CommonUtils.isNull(member);
    }

    public static boolean isNotMatchingPassword(String inputPassword, String originalPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return !encoder.matches(inputPassword, originalPassword);
    }

    public static String makeRandomNickname() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static HttpHeaders makeHttpHeadersWhenSignupRedirect(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();

        String url = "/member/login";
        String token = JwtAuthenticationFilter.resolveToken(request);

        httpHeaders.setBearerAuth(token);
        httpHeaders.setLocation(URI.create(url));

        return httpHeaders;
    }

    public static String encodePassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
}
