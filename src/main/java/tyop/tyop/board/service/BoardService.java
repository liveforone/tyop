package tyop.tyop.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tyop.tyop.board.dto.BoardResponse;
import tyop.tyop.board.model.Board;
import tyop.tyop.board.model.BoardState;
import tyop.tyop.board.repository.BoardRepository;
import tyop.tyop.board.util.BoardMapper;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

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

    public Board getBoardEntity(Long boardId) {
        return boardRepository.findOneBoard(boardId);
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
