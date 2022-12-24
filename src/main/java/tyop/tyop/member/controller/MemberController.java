package tyop.tyop.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tyop.tyop.jwt.TokenInfo;
import tyop.tyop.member.dto.ChangeEmailRequest;
import tyop.tyop.member.dto.ChangePasswordRequest;
import tyop.tyop.member.dto.MemberRequest;
import tyop.tyop.member.dto.MemberResponse;
import tyop.tyop.member.model.Member;
import tyop.tyop.member.model.Role;
import tyop.tyop.member.service.MemberService;
import tyop.tyop.member.util.MemberUtils;
import tyop.tyop.utility.CommonUtils;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok("home");
    }

    @GetMapping("/member/signup")
    public ResponseEntity<?> signupPage() {
        return ResponseEntity.ok("회원가입페이지");
    }

    @PostMapping("/member/signup")
    public ResponseEntity<?> signup(
            @RequestBody MemberRequest memberRequest,
            HttpServletRequest request
    ) {
        if (MemberUtils.isDuplicateEmail(
                memberService.getMemberEntity(memberRequest.getEmail())
        )) {
            log.info("이메일이 중복됨.");
            return ResponseEntity.ok("중복되는 이메일이 있어 회원가입이 불가능합니다.");
        }

        memberService.signup(memberRequest);
        log.info("회원 가입 성공");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(MemberUtils.makeHttpHeadersWhenSignupRedirect(request))
                .build();
    }

    @GetMapping("/member/login")
    public ResponseEntity<?> loginPage() {
        return ResponseEntity.ok("로그인 페이지");
    }

    @PostMapping("/member/login")
    public ResponseEntity<?> login(@RequestBody MemberRequest memberRequest) {
        Member member = memberService.getMemberEntity(memberRequest.getEmail());

        if (CommonUtils.isNull(member)) {
            log.info("잘못된 이메일.");
            return ResponseEntity.ok("회원 조회가 되지않아 로그인이 불가능합니다.");
        }

        if (MemberUtils.isNotMatchingPassword(
                memberRequest.getPassword(),
                member.getPassword()
        )) {
            log.info("비밀번호가 일치하지 않음.");
            return ResponseEntity.ok("비밀번호가 다릅니다. 다시 시도하세요.");
        }

        TokenInfo tokenInfo = memberService.login(memberRequest);
        log.info("로그인 성공");

        return ResponseEntity.ok(tokenInfo);
    }

    @GetMapping("/member/my-page")
    public ResponseEntity<MemberResponse> myPage(Principal principal) {
        MemberResponse member = memberService.getMemberDto(principal.getName());

        return ResponseEntity.ok(member);
    }

    @PostMapping("/member/change-nickname")
    public ResponseEntity<?> changeNickname(
            @RequestBody String nickname,
            Principal principal,
            HttpServletRequest request
    ) {
        Member member = memberService.getMemberByNickname(nickname);

        if (MemberUtils.isDuplicateNickname(member)) {
            log.info("닉네임 중복됨");
            return ResponseEntity.ok("중복되는 닉네임 입니다.\n 다시 입력하세요.");
        }

        memberService.updateNickname(principal.getName(), nickname);
        log.info("닉네임 변경 성공");

        String url = "/member/logout";

        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @PostMapping("/member/change-email")
    public ResponseEntity<?> changeEmail(
            @RequestBody ChangeEmailRequest changeEmailRequest,
            Principal principal,
            HttpServletRequest request
    ) {
        Member member = memberService.getMemberEntity(principal.getName());

        if (MemberUtils.isDuplicateEmail(
                memberService.getMemberEntity(changeEmailRequest.getEmail())
        )) {
            log.info("이메일이 중복됨.");
            return ResponseEntity
                    .ok("해당 이메일이 이미 존재합니다. 다시 입력해주세요");
        }

        if (MemberUtils.isNotMatchingPassword(
                changeEmailRequest.getPassword(),
                member.getPassword()
        )) {
            log.info("비밀번호 일치하지 않음.");
            return ResponseEntity.ok("비밀번호가 다릅니다. 다시 입력해주세요.");
        }

        memberService.updateEmail(
                principal.getName(),
                changeEmailRequest.getEmail()
        );
        log.info("이메일 변경 성공");

        String url = "/member/logout";

        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }


    @PostMapping("/member/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest,
            Principal principal,
            HttpServletRequest request
    ) {
        Member member = memberService.getMemberEntity(principal.getName());

        if (MemberUtils.isNotMatchingPassword(
                changePasswordRequest.getOldPassword(),
                member.getPassword()
        )) {
            log.info("비밀번호 일치하지 않음.");
            return ResponseEntity.ok("비밀번호가 다릅니다. 다시 입력해주세요.");
        }

        memberService.updatePassword(
                member.getId(),
                changePasswordRequest.getNewPassword()
        );
        log.info("비밀번호 변경 성공");

        String url = "/member/logout";

        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @PostMapping("/member/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestBody String password,
            Principal principal
    ) {
        Member member = memberService.getMemberEntity(principal.getName());

        if (MemberUtils.isNotMatchingPassword(password, member.getPassword())) {
            log.info("비밀번호 일치하지 않음.");
            return ResponseEntity.ok("비밀번호가 다릅니다. 다시 입력해주세요.");
        }

        Long memberId = member.getId();
        log.info("회원 : " + memberId + " 탈퇴 성공");
        memberService.deleteUser(memberId);

        return ResponseEntity.ok("그동안 서비스를 이용해주셔서 감사합니다.");
    }

    @GetMapping("/admin")
    public ResponseEntity<?> admin(Principal principal) {
        Member member = memberService.getMemberEntity(principal.getName());

        if (!member.getAuth().equals(Role.ADMIN)) {  //권한 검증
            log.info("어드민 페이지 접속에 실패했습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("어드민이 어드민 페이지에 접속했습니다.");
        return ResponseEntity.ok(
                memberService.getAllMemberForAdmin()
        );
    }

    @GetMapping("/member/prohibition")
    public ResponseEntity<?> prohibition() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("접근 권한이 없습니다.");
    }
}
