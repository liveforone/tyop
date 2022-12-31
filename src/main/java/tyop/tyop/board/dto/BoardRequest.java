package tyop.tyop.board.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import tyop.tyop.board.model.BoardState;
import tyop.tyop.member.model.Member;

@Data
@NoArgsConstructor
public class BoardRequest {

    private Long id;
    @NotNull
    @Size(max = 50)
    private String title;
    @Size(max = 500)
    private String content;
    private int hit;
    private String tag;
    private BoardState boardState;
    private Member member;
}
