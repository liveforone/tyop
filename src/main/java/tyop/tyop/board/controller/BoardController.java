package tyop.tyop.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import tyop.tyop.board.dto.BoardResponse;
import tyop.tyop.board.model.Board;
import tyop.tyop.board.service.BoardService;
import tyop.tyop.board.util.BoardMapper;
import tyop.tyop.utility.CommonUtils;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board/hot")
    public ResponseEntity<?> hotBoards(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "hit", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {
        Page<BoardResponse> hotBoards = boardService.getHotBoards(pageable);

        return ResponseEntity.ok(hotBoards);
    }

    @GetMapping("/board/post")
    public ResponseEntity<?> boardPostPage() {
        return ResponseEntity.ok("게시글 생성 페이지입니다.");
    }

//    @PostMapping("/board/post")
//    public ResponseEntity<?> boardPost(
//            Principal principal
//            //멀티팔트 파일
//    ) {
//        //파일 만들고 파일 저장 넣기
//    }

    @GetMapping("/board/{id}")
    public ResponseEntity<?> boardDetail(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        Board board = boardService.getBoardEntity(id);

        if (CommonUtils.isNull(board)) {
            return ResponseEntity.ok("게시글이 존재하지 않습니다.");
        }

        boardService.updateHit(id);
        log.info("hit update");

        Map<String, Object> map = new HashMap<>();
        map.put("board", BoardMapper.entityToDtoDetail(board));
        map.put("user", principal.getName());

        return ResponseEntity.ok(map);
    }
}
