package tyop.tyop.board.service;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tyop.tyop.board.dto.BoardRequest;
import tyop.tyop.board.model.BoardState;
import tyop.tyop.member.dto.MemberRequest;
import tyop.tyop.member.service.MemberService;

@SpringBootTest
@Transactional(readOnly = true)
class BoardServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private EntityManager em;

    @Transactional
    public void createMember(String email) {
        MemberRequest dto = new MemberRequest();
        dto.setEmail(email);
        dto.setPassword("1234");

        memberService.signup(dto);
    }

    @Transactional
    public Long createBoard() {
        String email = "yc1234@gmail.com";
        createMember(email);

        String title = "test_title";
        String content = "test_content";
        BoardRequest boardRequest = new BoardRequest();
        boardRequest.setTitle(title);
        boardRequest.setContent(content);
        return boardService.saveBoard(boardRequest, email);
    }

    @Test
    @Transactional
    void saveBoard() {
        //given
        String email = "yc1234@gmail.com";
        createMember(email);

        String title = "test_title";
        BoardRequest boardRequest = new BoardRequest();
        boardRequest.setTitle(title);
        boardRequest.setContent("test_content");

        //when
        Long boardId = boardService.saveBoard(boardRequest, email);
        em.flush();
        em.clear();

        //then
        Assertions
                .assertThat(boardService.getBoardEntity(boardId).getTitle())
                .isEqualTo(title);
    }

    @Test
    @Transactional
    void editBoard() {
        //given
        Long boardId = createBoard();

        //when
        BoardRequest updateRequest = new BoardRequest();
        String updateTitle = "updated_title";
        updateRequest.setTitle(updateTitle);
        updateRequest.setContent("test_content");
        boardService.editBoard(updateRequest, boardId);
        em.flush();
        em.clear();

        //then
        Assertions
                .assertThat(boardService.getBoardEntity(boardId).getTitle())
                .isEqualTo(updateTitle);
    }

    @Test
    @Transactional
    void reportBoard() {
        //given
        Long boardId = createBoard();

        //when
        boardService.reportBoard(boardId);
        em.flush();
        em.clear();

        //then
        Assertions
                .assertThat(boardService.getBoardEntity(boardId).getBoardState())
                .isEqualTo(BoardState.BLOCK);
    }
}