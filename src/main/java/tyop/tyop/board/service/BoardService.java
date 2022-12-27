package tyop.tyop.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tyop.tyop.board.dto.BoardResponse;
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
        BoardState normalState = BoardState.NORMAL;

        return BoardMapper.entityToDtoPage(
                boardRepository.findHotBoards(nowDate, normalState, pageable)
        );
    }
}
