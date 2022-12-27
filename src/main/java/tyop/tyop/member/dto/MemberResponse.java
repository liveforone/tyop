package tyop.tyop.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tyop.tyop.member.model.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String email;
    private String nickname;
    private Role auth;
    private int blockCount;
    private String introduction;
}
