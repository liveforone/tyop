package tyop.tyop.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tyop.tyop.board.dto.BoardResponse;
import tyop.tyop.board.model.Board;
import tyop.tyop.board.model.BoardState;
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

    @GetMapping("/board/search-title")
    public ResponseEntity<?> searchTitlePage(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @RequestParam("keyword") String keyword
    ) {
        Page<BoardResponse> boards = boardService.searchBoardsByTitle(keyword, pageable);

        return ResponseEntity.ok(boards);
    }

    @GetMapping("/board/search-tag")
    public ResponseEntity<?> searchTagPage(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @RequestParam("keyword") String keyword
    ) {
        Page<BoardResponse> boards = boardService.searchBoardsByTag(keyword, pageable);

        return ResponseEntity.ok(boards);
    }

    @GetMapping("/board/my-board")
    public ResponseEntity<?> myFeed(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            Principal principal
    ) {
        Page<BoardResponse> boards = boardService.getBoardsByEmail(principal.getName(), pageable);

        return ResponseEntity.ok(boards);
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

        if (board.getBoardState() == BoardState.BLOCK) {
            return ResponseEntity.ok("신고된 게시글입니다.");
        }

        boardService.updateHit(id);
        log.info("hit update");

        Map<String, Object> map = new HashMap<>();
        map.put("board", BoardMapper.entityToDtoDetail(board));
        map.put("user", principal.getName());

        return ResponseEntity.ok(map);
    }

    @PostMapping("/board/report/{id}")
    public ResponseEntity<?> reportBoard(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ) {
        Board board = boardService.getBoardEntity(id);

        if (CommonUtils.isNull(board)) {
            return ResponseEntity.ok("존재하지 않는 게시글입니다.");
        }

        boardService.reportBoard(id);
        log.info("게시글 신고 성공");

        String url = "/board/hot";
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }
}
