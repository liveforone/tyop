package tyop.tyop.comment.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tyop.tyop.board.model.Board;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String writer;

    @Enumerated(value = EnumType.STRING)
    private CommentState commentState;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate createdDate;

    @Builder
    public Comment(
            Long id,
            String content,
            String writer,
            CommentState commentState,
            Board board
    ) {
        this.id = id;
        this.content = content;
        this.writer = writer;
        this.commentState = commentState;
        this.board = board;
    }
}
