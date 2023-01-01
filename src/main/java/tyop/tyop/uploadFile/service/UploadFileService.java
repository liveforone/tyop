package tyop.tyop.uploadFile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tyop.tyop.board.model.Board;
import tyop.tyop.uploadFile.dto.UploadFileRequest;
import tyop.tyop.uploadFile.dto.UploadFileResponse;
import tyop.tyop.uploadFile.model.UploadFile;
import tyop.tyop.uploadFile.repository.UploadFileRepository;
import tyop.tyop.uploadFile.util.UploadFileMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UploadFileService {

    private final UploadFileRepository uploadFileRepository;

    public List<UploadFileResponse> getFiles(Long boardId) {
        return UploadFileMapper.entityToDtoList(
                uploadFileRepository.findFilesByBoardId(boardId)
        );
    }

    @Transactional
    public void saveFile(MultipartFile uploadFile, Board board) throws IOException {
        UUID uuid = UUID.randomUUID();
        String saveFileName = uuid + "_" + uploadFile.getOriginalFilename();

        uploadFile.transferTo(new File(saveFileName));

        UploadFileRequest dto = UploadFileRequest.builder()
                .saveFileName(saveFileName)
                .board(board)
                .build();
        uploadFileRepository.save(UploadFileMapper.dtoToEntity(dto));
    }

    @Transactional
    public void deleteFile(Long boardId, Board board) {
        List<UploadFile> files = uploadFileRepository.findFilesByBoardId(boardId);

        for (UploadFile uploadFile : files) {
            String saveFileName = uploadFile.getSaveFileName();
            File file = new File("C:\\Temp\\upload\\" + saveFileName);
            if (file.delete()) {
                log.info("file : " + saveFileName + " 삭제 완료");
            }
        }
        uploadFileRepository.deleteBulkFileByBoardId(board);
    }
}
