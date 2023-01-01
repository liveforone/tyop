package tyop.tyop.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tyop.tyop.board.model.Board;
import tyop.tyop.comment.dto.CommentEditRequest;
import tyop.tyop.comment.dto.CommentRequest;
import tyop.tyop.comment.dto.CommentResponse;
import tyop.tyop.comment.model.Comment;
import tyop.tyop.comment.model.CommentState;
import tyop.tyop.comment.repository.CommentRepository;
import tyop.tyop.comment.util.CommentMapper;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;

    public Page<CommentResponse> getComments(Long boardId, Pageable pageable) {
        return CommentMapper.entityToDtoPage(
                commentRepository.findCommentsByBoardId(
                        CommentState.NORMAL,
                        boardId,
                        pageable
                )
        );
    }

    public Comment getCommentEntity(Long commentId) {
        return commentRepository.findCommentById(CommentState.NORMAL, commentId);
    }

    @Transactional
    public void saveComment(CommentRequest commentRequest, String email, Board board) {
        commentRequest.setWriter(email);
        commentRequest.setCommentState(CommentState.NORMAL);
        commentRequest.setBoard(board);

        commentRepository.save(
                CommentMapper.dtoToEntity(commentRequest)
        );
    }

    @Transactional
    public void editComment(CommentEditRequest commentEditRequest, Long commentId) {
        commentRepository.editComment(commentEditRequest.getContent(), commentId);
    }

    @Transactional
    public void reportComment(Long commentId) {
        commentRepository.reportComment(CommentState.BLOCK, commentId);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void bulkDeleteComment(Board board) {
        commentRepository.deleteBulkCommentByBoardId(board);
    }
}
