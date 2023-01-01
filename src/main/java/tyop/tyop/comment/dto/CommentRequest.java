package tyop.tyop.comment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import tyop.tyop.board.model.Board;
import tyop.tyop.comment.model.CommentState;

@Data
@NoArgsConstructor
public class CommentRequest {

    private Long id;
    @NotNull
    @Size(max = 100)
    private String content;
    @NotNull
    private String writer;
    private CommentState commentState;
    private Board board;
}
