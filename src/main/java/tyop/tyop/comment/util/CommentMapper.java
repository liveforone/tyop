package tyop.tyop.comment.util;

import org.springframework.data.domain.Page;
import tyop.tyop.comment.dto.CommentRequest;
import tyop.tyop.comment.dto.CommentResponse;
import tyop.tyop.comment.model.Comment;

public class CommentMapper {

    public static Comment dtoToEntity(CommentRequest commentRequest) {
        return Comment.builder()
                .id(commentRequest.getId())
                .content(commentRequest.getContent())
                .writer(commentRequest.getWriter())
                .commentState(commentRequest.getCommentState())
                .board(commentRequest.getBoard())
                .build();
    }

    public static CommentResponse dtoBuilder(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .writer(comment.getWriter())
                .createdDate(comment.getCreatedDate())
                .build();
    }

    public static Page<CommentResponse> entityToDtoPage(Page<Comment> comments) {
        return comments.map(CommentMapper::dtoBuilder);
    }
}
