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
import tyop.tyop.board.util.BoardMapper;
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
    private static final int LIMIT_UPLOAD_SIZE = 4;

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
                    .body("제목은 50자, 게시글은 500자의 길이를 초과해선 안됩니다.");
        }

        String content = boardRequest.getContent();
        if (FilteringBot.ignoreBlankCheckBadWord(content)) {
            memberService.plusBlockCount(email);
            return ResponseEntity
                    .ok("비속어가 포함된 게시글입니다.. \n올바른 단어를 사용하세요.");
        }

        Long boardId = boardService.saveBoard(boardRequest, email);
        log.info("게시글 저장 성공");

        if (!CommonUtils.isEmptyMultipartFile(uploadFile)) {
            Board board = boardService.getBoardEntity(boardId);
            int cnt = 0;
            for (MultipartFile file : uploadFile) {
                if (cnt == LIMIT_UPLOAD_SIZE) {
                    break;
                }
                uploadFileService.saveFile(file, board);
                log.info("파일 저장 성공");
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
            return ResponseEntity.ok("게시글이 존재하지 않습니다.");
        }

        if (board.getBoardState() != BoardState.NORMAL) {
            return ResponseEntity.ok("볼수 없는 게시글입니다.");
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
            return ResponseEntity.ok("존재하지 않는 게시글입니다.");
        }

        boardService.reportBoard(id);
        log.info("게시글 신고 성공");

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
            return ResponseEntity.ok("존재하지 않는 게시글입니다.");
        }

        String email = principal.getName();
        String writer = board.getMember().getEmail();
        if (!Objects.equals(email, writer)) {
            return ResponseEntity.ok("작성자가 아니면 수정이 불가능합니다.");
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
            return ResponseEntity.ok("존재하지 않는 게시글입니다.");
        }

        String email = principal.getName();
        String writer = board.getMember().getEmail();
        if (!Objects.equals(email, writer)) {
            return ResponseEntity.ok("작성자가 아니면 수정이 불가능합니다.");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("제목은 50자, 게시글은 500자의 길이를 초과해선 안됩니다.");
        }

        String content = boardRequest.getContent();
        if (FilteringBot.ignoreBlankCheckBadWord(content)) {
            memberService.plusBlockCount(email);
            return ResponseEntity
                    .ok("비속어가 포함된 게시글입니다.. \n올바른 단어를 사용하세요.");
        }

        boardService.editBoard(boardRequest, id);
        log.info("게시글 수정 완료");

        if (!CommonUtils.isEmptyMultipartFile(uploadFile)) {
            uploadFileService.deleteFile(id);

            int cnt = 0;
            for (MultipartFile file : uploadFile) {
                if (file.isEmpty() || cnt == LIMIT_UPLOAD_SIZE) {
                    break;
                }
                uploadFileService.saveFile(file, board);
                log.info("파일 저장 성공");
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
        return ResponseEntity.ok("문의할점을 적어주시고, \n게시글 복구 신청의 경우 게시글의 번호와 문제점이 없다는 이유를 상세히 적어주십시요.");
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
            return ResponseEntity.ok("게시글이 존재하지 않습니다.");
        }

        String writer = board.getMember().getEmail();
        String email = principal.getName();
        Member member = memberService.getMemberEntity(email);
        if (!Objects.equals(writer, email)
                || member.getAuth() != Role.ADMIN) {
            return ResponseEntity.ok("작성자가 아니면 문의 게시글을 볼 수 없습니다.");
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
            return ResponseEntity.ok("게시글이 존재하지 않습니다.");
        }

        String writer = board.getMember().getEmail();
        String email = principal.getName();
        if (!Objects.equals(writer, email)) {
            return ResponseEntity.ok("작성자가 아니면 삭제가 불가능합니다.");
        }

        uploadFileService.deleteFile(id);
        boardService.deleteBoard(id);
        log.info("게시글과 파일 모두 삭제 완료");

        String url = "/board/hot";
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }
}
