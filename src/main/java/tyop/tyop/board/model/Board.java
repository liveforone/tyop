package tyop.tyop.board.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tyop.tyop.member.model.Member;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private int hit;

    private String tag;

    @Enumerated(value = EnumType.STRING)
    private BoardState boardState;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate createdDate;

    @Builder
    public Board(
            Long id,
            String title,
            String content,
            int hit,
            String tag,
            BoardState boardState,
            Member member
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.hit = hit;
        this.tag = tag;
        this.boardState = boardState;
        this.member = member;
    }
}
