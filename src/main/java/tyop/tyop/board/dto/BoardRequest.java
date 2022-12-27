package tyop.tyop.board.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import tyop.tyop.board.model.BoardState;
import tyop.tyop.member.model.Member;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardRequest {

    private Long id;
    private String title;
    private String content;
    private int hit;
    private String tag;
    private BoardState boardState;
    private Member member;
}
