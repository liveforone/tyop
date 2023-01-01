package tyop.tyop.comment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentEditRequest {

    @NotNull
    @Size(max = 100)
    private String content;
}
