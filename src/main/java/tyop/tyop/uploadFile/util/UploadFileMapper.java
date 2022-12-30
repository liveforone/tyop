package tyop.tyop.uploadFile.util;

import tyop.tyop.uploadFile.dto.UploadFileRequest;
import tyop.tyop.uploadFile.dto.UploadFileResponse;
import tyop.tyop.uploadFile.model.UploadFile;

import java.util.List;
import java.util.stream.Collectors;

public class UploadFileMapper {

    public static UploadFile dtoToEntity(UploadFileRequest uploadFileRequest) {
        return UploadFile.builder()
                .id(uploadFileRequest.getId())
                .saveFileName(uploadFileRequest.getSaveFileName())
                .board(uploadFileRequest.getBoard())
                .build();
    }

    private static UploadFileResponse dtoBuilder(UploadFile uploadFile) {
        return UploadFileResponse.builder()
                .id(uploadFile.getId())
                .saveFileName(uploadFile.getSaveFileName())
                .build();
    }

    public static List<UploadFileResponse> entityToDtoList(List<UploadFile> uploadFiles) {
        return uploadFiles
                .stream()
                .map(UploadFileMapper::dtoBuilder)
                .collect(Collectors.toList());
    }
}
