package tyop.tyop.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tyop.tyop.board.dto.BoardRequest;
import tyop.tyop.board.dto.BoardResponse;
import tyop.tyop.board.model.Board;
import tyop.tyop.board.model.BoardState;
import tyop.tyop.board.repository.BoardRepository;
import tyop.tyop.board.util.BoardMapper;
import tyop.tyop.member.model.Member;
import tyop.tyop.member.repository.MemberRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    public Page<BoardResponse> getHotBoards(Pageable pageable) {
        LocalDate nowDate = LocalDate.now();

        return BoardMapper.entityToDtoPage(
                boardRepository.findHotBoards(nowDate, pageable)
        );
    }

    public Page<BoardResponse> searchBoardsByTitle(String keyword, Pageable pageable) {
        return BoardMapper.entityToDtoPage(
                boardRepository.searchBoardsByTitle(keyword, pageable)
        );
    }

    public Page<BoardResponse> searchBoardsByTag(String keyword, Pageable pageable) {
        return BoardMapper.entityToDtoPage(
                boardRepository.searchBoardsByTag(keyword, pageable)
        );
    }

    public Page<BoardResponse> getBoardsByEmail(String email, Pageable pageable) {
        return BoardMapper.entityToDtoPage(
                boardRepository.findBoardsByEmail(email, pageable)
        );
    }

    public List<BoardResponse> getInquiryBoards(String email) {
        return BoardMapper.entityToDtoList(
                boardRepository.findInquiryBoards(BoardState.INQUIRY, email)
        );
    }

    public Board getBoardEntity(Long boardId) {
        return boardRepository.findOneBoard(boardId);
    }

    public Board getInquiryBoardEntity(Long boardId) {
        return boardRepository.findOneInquiryBoard(boardId);
    }

    @Transactional
    public Long saveBoard(BoardRequest boardRequest, String email) {
        Member member = memberRepository.findByEmail(email);
        boardRequest.setBoardState(BoardState.NORMAL);
        boardRequest.setMember(member);

        return boardRepository.save(
                BoardMapper.dtoToEntity(boardRequest)
        ).getId();
    }

    @Transactional
    public void editBoard(BoardRequest boardRequest, Long boardId) {
        Board board = boardRepository.findOneBoard(boardId);

        boardRequest.setId(board.getId());
        boardRequest.setHit(board.getHit());
        boardRequest.setBoardState(board.getBoardState());
        boardRequest.setMember(board.getMember());

        boardRepository.save(
                BoardMapper.dtoToEntity(boardRequest)
        );
    }

    @Transactional
    public Long saveInquiryBoard(BoardRequest boardRequest, String email) {
        Member member = memberRepository.findByEmail(email);
        boardRequest.setBoardState(BoardState.INQUIRY);
        boardRequest.setMember(member);

        return boardRepository.save(
                BoardMapper.dtoToEntity(boardRequest)
        ).getId();
    }

    @Transactional
    public void updateHit(Long boardId) {
        boardRepository.updateHit(boardId);
    }

    @Transactional
    public void reportBoard(Long boardId) {
        boardRepository.reportBoard(BoardState.BLOCK, boardId);
    }
}
