package tyop.tyop.uploadFile.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tyop.tyop.board.model.Board;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String saveFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @Builder
    public UploadFile(Long id, String saveFileName, Board board) {
        this.id = id;
        this.saveFileName = saveFileName;
        this.board = board;
    }
}
