package tyop.tyop.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tyop.tyop.board.dto.BoardRequest;
import tyop.tyop.board.dto.BoardResponse;
import tyop.tyop.board.model.Board;
import tyop.tyop.board.model.BoardState;
import tyop.tyop.board.service.BoardService;
import tyop.tyop.board.util.BoardConstants;
import tyop.tyop.board.util.BoardMapper;
import tyop.tyop.comment.service.CommentService;
import tyop.tyop.filteringBot.FilteringBot;
import tyop.tyop.member.model.Member;
import tyop.tyop.member.model.Role;
import tyop.tyop.member.service.MemberService;
import tyop.tyop.uploadFile.service.UploadFileService;
import tyop.tyop.utility.CommonUtils;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final MemberService memberService;
    private final UploadFileService uploadFileService;
    private final CommentService commentService;

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
        String email = principal.getName();
        Page<BoardResponse> boards = boardService.getBoardsByEmail(email, pageable);

        return ResponseEntity.ok(boards);
    }

    @GetMapping("/board/post")
    public ResponseEntity<?> boardPostPage() {
        return ResponseEntity.ok("????????? ?????? ??????????????????.");
    }

    @PostMapping("/board/post")
    public ResponseEntity<?> boardPost(
            @RequestPart List<MultipartFile> uploadFile,
            @RequestPart("boardRequest") @Valid BoardRequest boardRequest,
            Principal principal,
            BindingResult bindingResult,
            HttpServletRequest request
    ) throws IllegalStateException, IOException {
        String email = principal.getName();

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("????????? 50???, ???????????? 500?????? ????????? ???????????? ????????????.");
        }

        String content = boardRequest.getContent();
        if (FilteringBot.ignoreBlankCheckBadWord(content)) {
            memberService.plusBlockCount(email);
            return ResponseEntity
                    .ok("???????????? ????????? ??????????????????.. \n????????? ????????? ???????????????.");
        }

        Long boardId = boardService.saveBoard(boardRequest, email);
        log.info("????????? ?????? ??????");

        if (!CommonUtils.isEmptyMultipartFile(uploadFile)) {
            Board board = boardService.getBoardEntity(boardId);
            int cnt = 0;
            for (MultipartFile file : uploadFile) {
                if (cnt == BoardConstants.LIMIT_UPLOAD_SIZE.getValue()) {
                    break;
                }
                uploadFileService.saveFile(file, board);
                log.info("?????? ?????? ??????");
                cnt++;
            }
        }

        String url = "/board/" + boardId;
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @GetMapping("/board/{id}")
    public ResponseEntity<?> boardDetail(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        Board board = boardService.getBoardEntity(id);

        if (CommonUtils.isNull(board)) {
            return ResponseEntity.ok("???????????? ???????????? ????????????.");
        }

        if (board.getBoardState() != BoardState.NORMAL) {
            return ResponseEntity.ok("?????? ?????? ??????????????????.");
        }

        boardService.updateHit(id);
        log.info("hit update");

        Map<String, Object> map = new HashMap<>();
        map.put("board", BoardMapper.entityToDtoDetail(board));
        map.put("user", principal.getName());
        map.put("file", uploadFileService.getFiles(id));

        return ResponseEntity.ok(map);
    }

    @PostMapping("/board/report/{id}")
    public ResponseEntity<?> reportBoard(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ) {
        Board board = boardService.getBoardEntity(id);

        if (CommonUtils.isNull(board)) {
            return ResponseEntity.ok("???????????? ?????? ??????????????????.");
        }

        boardService.reportBoard(id);
        log.info("????????? ?????? ??????");

        String url = "/board/hot";
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @GetMapping("/board/edit/{id}")
    public ResponseEntity<?> editBoardPage(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        Board board = boardService.getBoardEntity(id);

        if (CommonUtils.isNull(board)) {
            return ResponseEntity.ok("???????????? ?????? ??????????????????.");
        }

        String email = principal.getName();
        String writer = board.getMember().getEmail();
        if (!Objects.equals(email, writer)) {
            return ResponseEntity.ok("???????????? ????????? ????????? ??????????????????.");
        }

        return ResponseEntity.ok(BoardMapper.entityToDtoDetail(board));
    }

    @PostMapping("/board/edit/{id}")
    public ResponseEntity<?> editBoard(
            @PathVariable("id") Long id,
            @RequestPart List<MultipartFile> uploadFile,
            @RequestPart("boardRequest") @Valid BoardRequest boardRequest,
            Principal principal,
            BindingResult bindingResult,
            HttpServletRequest request
    ) throws IOException {
        Board board = boardService.getBoardEntity(id);

        if (CommonUtils.isNull(board)) {
            return ResponseEntity.ok("???????????? ?????? ??????????????????.");
        }

        String email = principal.getName();
        String writer = board.getMember().getEmail();
        if (!Objects.equals(email, writer)) {
            return ResponseEntity.ok("???????????? ????????? ????????? ??????????????????.");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("????????? 50???, ???????????? 500?????? ????????? ???????????? ????????????.");
        }

        String content = boardRequest.getContent();
        if (FilteringBot.ignoreBlankCheckBadWord(content)) {
            memberService.plusBlockCount(email);
            return ResponseEntity
                    .ok("???????????? ????????? ??????????????????.. \n????????? ????????? ???????????????.");
        }

        boardService.editBoard(boardRequest, id);
        log.info("????????? ?????? ??????");

        if (!CommonUtils.isEmptyMultipartFile(uploadFile)) {
            uploadFileService.deleteFile(id, board);

            int cnt = 0;
            for (MultipartFile file : uploadFile) {
                if (file.isEmpty() || cnt == BoardConstants.LIMIT_UPLOAD_SIZE.getValue()) {
                    break;
                }
                uploadFileService.saveFile(file, board);
                log.info("?????? ?????? ??????");
                cnt = cnt + 1;
            }
        }

        String url = "/board/" + id;
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @GetMapping("/board/inquiry")
    public ResponseEntity<?> inquiryBoards(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            Principal principal
    ) {
        String email = principal.getName();
        Member member = memberService.getMemberEntity(email);

        if (member.getAuth() == Role.ADMIN) {
            return ResponseEntity.ok(boardService.getAllInquiryBoards(pageable));
        }

        Page<BoardResponse> boards = boardService.getInquiryBoards(email, pageable);
        return ResponseEntity.ok(boards);
    }

    @GetMapping("/board/inquiry/post")
    public ResponseEntity<?> inquiryBoardPostPage() {
        return ResponseEntity.ok("??????????????? ???????????????, \n????????? ?????? ????????? ?????? ???????????? ????????? ???????????? ????????? ????????? ????????? ??????????????????.");
    }

    @PostMapping("/board/inquiry/post")
    public ResponseEntity<?> inquiryBoardPost(
            @RequestBody @Valid BoardRequest boardRequest,
            Principal principal,
            HttpServletRequest request
    ) {
        Long boardId = boardService.saveInquiryBoard(boardRequest, principal.getName());

        String url = "/board/inquiry/" + boardId;
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @GetMapping("/board/inquiry/{id}")
    public ResponseEntity<?> inquiryBoardDetail(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        Board board = boardService.getInquiryBoardEntity(id);

        if (CommonUtils.isNull(board)) {
            return ResponseEntity.ok("???????????? ???????????? ????????????.");
        }

        String writer = board.getMember().getEmail();
        String email = principal.getName();
        Member member = memberService.getMemberEntity(email);
        if (!Objects.equals(writer, email)
                || member.getAuth() != Role.ADMIN) {
            return ResponseEntity.ok("???????????? ????????? ?????? ???????????? ??? ??? ????????????.");
        }

        return ResponseEntity.ok(BoardMapper.entityToDtoDetail(board));
    }

    @PostMapping("/board/delete/{id}")
    public ResponseEntity<?> deleteBoard(
            @PathVariable("id") Long id,
            Principal principal,
            HttpServletRequest request
    ) {
        Board board = boardService.getInquiryBoardEntity(id);

        if (CommonUtils.isNull(board)) {
            return ResponseEntity.ok("???????????? ???????????? ????????????.");
        }

        String writer = board.getMember().getEmail();
        String email = principal.getName();
        if (!Objects.equals(writer, email)) {
            return ResponseEntity.ok("???????????? ????????? ????????? ??????????????????.");
        }

        uploadFileService.deleteFile(id, board);
        commentService.bulkDeleteComment(board);
        boardService.deleteBoard(id);
        log.info("???????????? ?????? ?????? ?????? ??????");

        String url = "/board/hot";
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }
}
