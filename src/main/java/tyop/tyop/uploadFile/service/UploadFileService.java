package tyop.tyop.uploadFile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tyop.tyop.board.model.Board;
import tyop.tyop.board.repository.BoardRepository;
import tyop.tyop.uploadFile.dto.UploadFileRequest;
import tyop.tyop.uploadFile.dto.UploadFileResponse;
import tyop.tyop.uploadFile.repository.UploadFileRepository;
import tyop.tyop.uploadFile.util.UploadFileMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UploadFileService {

    private final UploadFileRepository uploadFileRepository;
    private final BoardRepository boardRepository;
    private static final int LIMIT_UPLOAD_SIZE = 4;

    public List<UploadFileResponse> getFiles(Long boardId) {
        return UploadFileMapper.entityToDtoList(
                uploadFileRepository.findFilesByBoardId(boardId)
        );
    }

    @Transactional
    public void saveFile(List<MultipartFile> uploadFile, Long boardId) throws IOException {
        Board board = boardRepository.findOneBoard(boardId);

        int size = uploadFile.size();

        if (size > 4) {

            for (int i=0; i<LIMIT_UPLOAD_SIZE; i++) {
                MultipartFile file = uploadFile.get(i);

                UUID uuid = UUID.randomUUID();
                String saveFileName = uuid + "_" + file.getOriginalFilename();

                file.transferTo(new File(saveFileName));

                UploadFileRequest dto = UploadFileRequest.builder()
                        .saveFileName(saveFileName)
                        .board(board)
                        .build();
                uploadFileRepository.save(UploadFileMapper.dtoToEntity(dto));
            }

        } else {

            for (MultipartFile file : uploadFile) {
                UUID uuid = UUID.randomUUID();
                String saveFileName = uuid + "_" + file.getOriginalFilename();

                file.transferTo(new File(saveFileName));

                UploadFileRequest dto = UploadFileRequest.builder()
                        .saveFileName(saveFileName)
                        .board(board)
                        .build();
                uploadFileRepository.save(UploadFileMapper.dtoToEntity(dto));
            }

        }
    }
}
