package tyop.tyop.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tyop.tyop.board.model.Board;
import tyop.tyop.comment.model.Comment;
import tyop.tyop.comment.model.CommentState;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c join c.board b where c.commentState = :commentState and b.id = :id")
    Page<Comment> findCommentsByBoardId(@Param("commentState") CommentState commentState, @Param("id") Long id, Pageable pageable);

    @Query("select c from Comment c join fetch c.board where c.commentState = :commentState and c.id = :id")
    Comment findCommentById(@Param("commentState") CommentState commentState, @Param("id") Long id);

    @Modifying
    @Query("update Comment c set c.content = :content where c.id = :id")
    void editComment(@Param("content") String content, @Param("id") Long id);

    @Modifying
    @Query("update Comment c set c.commentState = :commentState where c.id = :id")
    void reportComment(@Param("commentState") CommentState commentState, @Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("delete from Comment c where c.board = :board")
    void deleteBulkCommentByBoardId(@Param("board") Board board);
}