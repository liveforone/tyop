package tyop.tyop.uploadFile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tyop.tyop.board.model.Board;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileRequest {

    private Long id;
    private String saveFileName;
    private Board board;
}
