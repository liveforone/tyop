package tyop.tyop.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tyop.tyop.member.model.Member;
import tyop.tyop.member.model.Role;
import tyop.tyop.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return createUserDetails(memberRepository.findByEmail(email));
    }

    private UserDetails createUserDetails(Member member) {
        if (member.getAuth() == Role.ADMIN) {
            return User.builder()
                    .username(member.getEmail())
                    .password(member.getPassword())
                    .roles("ADMIN")
                    .build();
        }

        if (member.getAuth() == Role.MEMBER) {
            return User.builder()
                    .username(member.getEmail())
                    .password(member.getPassword())
                    .roles("MEMBER")
                    .build();
        }

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles("BLOCK")
                .build();
    }
}
