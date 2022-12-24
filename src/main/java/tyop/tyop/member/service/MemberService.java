package tyop.tyop.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tyop.tyop.jwt.JwtTokenProvider;
import tyop.tyop.jwt.TokenInfo;
import tyop.tyop.member.dto.MemberRequest;
import tyop.tyop.member.dto.MemberResponse;
import tyop.tyop.member.model.Member;
import tyop.tyop.member.model.Role;
import tyop.tyop.member.repository.MemberRepository;
import tyop.tyop.member.util.MemberMapper;
import tyop.tyop.member.util.MemberUtils;
import tyop.tyop.utility.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public Member getMemberEntity(String email) {
        return memberRepository.findByEmail(email);
    }

    public MemberResponse getMemberDto(String email) {
        Member member = memberRepository.findByEmail(email);

        if (CommonUtils.isNull(member)) {
            return null;
        }

        return MemberMapper.dtoBuilder(member);
    }

    public Member getMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname);
    }

    /*
     * 모든 유저 반환
     * when : 권한이 어드민인 유저가 호출할때
     */
    public List<Member> getAllMemberForAdmin() {
        return memberRepository.findAll();
    }

    @Transactional
    public void signup(MemberRequest memberRequest) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberRequest.setPassword(passwordEncoder.encode(
                memberRequest.getPassword()
        ));

        if (Objects.equals(memberRequest.getEmail(), "admin@tyop.com")) {
            memberRequest.setAuth(Role.ADMIN);
            memberRequest.setNickname("ADMIN");
        }

        memberRequest.setAuth(Role.MEMBER);
        memberRequest.setNickname(MemberUtils.makeRandomNickname());

        memberRepository.save(
                MemberMapper.dtoToEntity(memberRequest)
        );
    }

    @Transactional
    public TokenInfo login(MemberRequest memberRequest) {
        String email = memberRequest.getEmail();
        String password = memberRequest.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email,
                password
        );
        Authentication authentication = authenticationManagerBuilder
                .getObject()
                .authenticate(authenticationToken);

        return jwtTokenProvider
                .generateToken(authentication);
    }

    @Transactional
    public void blockMember(String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
        updatedAuthorities.add(new SimpleGrantedAuthority(Role.BLOCK.getValue()));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                auth.getPrincipal(),
                auth.getCredentials(),
                updatedAuthorities
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        memberRepository.blockMember(Role.BLOCK, email);
    }

    @Transactional
    public void updateEmail(String oldEmail, String newEmail) {
        memberRepository.updateEmail(oldEmail, newEmail);
    }

    @Transactional
    public void updatePassword(Long id, String inputPassword) {
        //pw 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String newPassword =  passwordEncoder.encode(inputPassword);

        memberRepository.updatePassword(id, newPassword);
    }

    @Transactional
    public void updateNickname(String email, String nickname) {
        memberRepository.updateNickname(email, nickname);
    }

    @Transactional
    public void plusBlockCount(String email) {
        memberRepository.plusBlockCount(email);
    }

    @Transactional
    public void minusBlockCount(String email) {
        memberRepository.minusBlockCount(email);
    }

    @Transactional
    public void deleteUser(Long id) {
        memberRepository.deleteById(id);
    }
}
