package tyop.tyop.board.util;

import org.springframework.data.domain.Page;
import tyop.tyop.board.dto.BoardRequest;
import tyop.tyop.board.dto.BoardResponse;
import tyop.tyop.board.model.Board;

import java.util.List;
import java.util.stream.Collectors;

public class BoardMapper {

    public static Board dtoToEntity(BoardRequest boardRequest) {
        return Board.builder()
                .id(boardRequest.getId())
                .title(boardRequest.getTitle())
                .content(boardRequest.getContent())
                .hit(boardRequest.getHit())
                .tag(boardRequest.getTag())
                .boardState(boardRequest.getBoardState())
                .member(boardRequest.getMember())
                .build();
    }

    public static BoardResponse dtoBuilder(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .hit(board.getHit())
                .tag(board.getTag())
                .writer(board.getMember().getNickname())
                .build();
    }

    public static Page<BoardResponse> entityToDtoPage(Page<Board> boards) {
        return boards.map(BoardMapper::dtoBuilder);
    }

    public static List<BoardResponse> entityToDtoList(List<Board> boards) {
        return boards
                .stream()
                .map(BoardMapper::dtoBuilder)
                .collect(Collectors.toList());
    }

    public static BoardResponse entityToDtoDetail(Board board) {
        return BoardMapper.dtoBuilder(board);
    }
}
