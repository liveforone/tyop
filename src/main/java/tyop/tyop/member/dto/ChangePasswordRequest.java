package tyop.tyop.member.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangePasswordRequest {

    private String oldPassword;
    private String newPassword;
}
