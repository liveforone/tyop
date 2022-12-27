package tyop.tyop.member.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import tyop.tyop.member.model.Role;

@Data
@NoArgsConstructor
public class MemberRequest {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private Role auth;
    private int blockCount;
    private String introduction;
}
