package tyop.tyop.comment.service;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tyop.tyop.board.dto.BoardRequest;
import tyop.tyop.board.model.Board;
import tyop.tyop.board.service.BoardService;
import tyop.tyop.comment.dto.CommentEditRequest;
import tyop.tyop.comment.dto.CommentRequest;
import tyop.tyop.comment.model.Comment;
import tyop.tyop.member.dto.MemberRequest;
import tyop.tyop.member.service.MemberService;

@SpringBootTest
@Transactional(readOnly = true)
class CommentServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EntityManager em;

    @Transactional
    private Long createComment() {
        String email = "yc1234@gmail.com";
        MemberRequest dto = new MemberRequest();
        dto.setEmail(email);
        dto.setPassword("1234");
        memberService.signup(dto);
        em.flush();
        em.clear();

        String title = "test_title";
        String content = "test_content";
        BoardRequest boardRequest = new BoardRequest();
        boardRequest.setTitle(title);
        boardRequest.setContent(content);
        Long boardId = boardService.saveBoard(boardRequest, email);
        em.flush();
        em.clear();

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("test content");
        Board board = boardService.getBoardEntity(boardId);
        return commentService.saveComment(commentRequest, email, board);
    }

    @Test
    @Transactional
    void editComment() {
        //given
        Long commentId = createComment();
        String updatedContent = "updated_content";

        //when
        CommentEditRequest commentEditRequest = new CommentEditRequest();
        commentEditRequest.setContent(updatedContent);
        commentService.editComment(commentEditRequest, commentId);
        em.flush();
        em.clear();

        //then
        Comment comment = commentService.getCommentEntity(commentId);
        Assertions
                .assertThat(comment.getContent())
                .isEqualTo(updatedContent);
    }

    @Test
    @Transactional
    void reportComment() {
        //given
        Long commentId = createComment();

        //when
        commentService.reportComment(commentId);
        em.flush();
        em.clear();

        //then
        /*
        * getEntity??? ?????? ????????? ????????? ???????????? ??????.
        * ????????? null????????? ?????????????????????.
         */
        Assertions
                .assertThat(commentService.getCommentEntity(commentId))
                .isEqualTo(null);
    }
}