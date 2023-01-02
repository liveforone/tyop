package tyop.tyop.comment.controller;

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
import tyop.tyop.board.model.Board;
import tyop.tyop.board.service.BoardService;
import tyop.tyop.comment.dto.CommentEditRequest;
import tyop.tyop.comment.dto.CommentRequest;
import tyop.tyop.comment.dto.CommentResponse;
import tyop.tyop.comment.model.Comment;
import tyop.tyop.comment.service.CommentService;
import tyop.tyop.filteringBot.FilteringBot;
import tyop.tyop.member.service.MemberService;
import tyop.tyop.utility.CommonUtils;

import java.security.Principal;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;
    private final MemberService memberService;

    @GetMapping("/comment/{boardId}")
    public ResponseEntity<?> commentPage(
            @PathVariable("boardId") Long boardId,
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {
        Page<CommentResponse> comments = commentService.getComments(boardId, pageable);

        return ResponseEntity.ok(comments);
    }

    @PostMapping("/comment/post/{boardId}")
    public ResponseEntity<?> commentPost(
            @PathVariable("boardId") Long boardId,
            @RequestBody @Valid CommentRequest commentRequest,
            Principal principal,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        Board board = boardService.getBoardEntity(boardId);

        if (CommonUtils.isNull(board)) {
            return ResponseEntity.ok("존재하지 않는 게시글 입니다.");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("댓글은 100자의 길이를 초과해선 안됩니다.");
        }

        String content = commentRequest.getContent();
        String email = principal.getName();
        if (FilteringBot.ignoreBlankCheckBadWord(content)) {
            memberService.plusBlockCount(email);
            return ResponseEntity
                    .ok("비속어가 포함된 게시글입니다.. \n올바른 단어를 사용하세요.");
        }

        commentService.saveComment(commentRequest, email, board);
        log.info("댓글 등록 성공");

        String url = "/comment/" + boardId;
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @PostMapping("/comment/edit/{commentId}")
    public ResponseEntity<?> commentEdit(
            @PathVariable("commentId") Long commentId,
            @RequestBody @Valid CommentEditRequest commentEditRequest,
            Principal principal,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        Comment comment = commentService.getCommentEntity(commentId);

        if (CommonUtils.isNull(comment)) {
            return ResponseEntity.ok("존재하지 않는 댓글 입니다.");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("댓글은 100자의 길이를 초과해선 안됩니다.");
        }

        String content = commentEditRequest.getContent();
        String email = principal.getName();
        if (FilteringBot.ignoreBlankCheckBadWord(content)) {
            memberService.plusBlockCount(email);
            return ResponseEntity
                    .ok("비속어가 포함된 게시글입니다.. \n올바른 단어를 사용하세요.");
        }

        commentService.editComment(commentEditRequest, commentId);
        log.info("댓글 수정 성공");

        Long boardId = comment.getBoard().getId();
        String url = "/comment/" + boardId;
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @PostMapping("/comment/report/{commentId}")
    public ResponseEntity<?> commentReport(
            @PathVariable("commentId") Long commentId,
            HttpServletRequest request
    ) {
        Comment comment = commentService.getCommentEntity(commentId);

        if (CommonUtils.isNull(comment)) {
            return ResponseEntity.ok("존재하지 않는 댓글입니다.");
        }

        commentService.reportComment(commentId);
        log.info("댓글 신고 성공");

        Long boardId = comment.getBoard().getId();
        String url = "/comment/" + boardId;
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @PostMapping("/comment/delete/{commentId}")
    public ResponseEntity<?> commentDelete(
            @PathVariable("commentId") Long commentId,
            Principal principal,
            HttpServletRequest request
    ) {
        Comment comment = commentService.getCommentEntity(commentId);

        if (CommonUtils.isNull(comment)) {
            return ResponseEntity.ok("존재하지 않는 댓글 입니다.");
        }

        String email = principal.getName();
        String writer = comment.getWriter();
        if (!Objects.equals(email, writer)) {
            return ResponseEntity.ok("작성자가 아니면 삭제가 불가능합니다.");
        }

        commentService.deleteComment(commentId);
        log.info("댓글 삭제 성공");


        Long boardId = comment.getBoard().getId();
        String url = "/comment/" + boardId;
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }
}