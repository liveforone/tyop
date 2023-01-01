package tyop.tyop.uploadFile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tyop.tyop.board.model.Board;
import tyop.tyop.uploadFile.model.UploadFile;

import java.util.List;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    @Query("select u from UploadFile u join fetch u.board b where b.id = :id")
    List<UploadFile> findFilesByBoardId(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("delete from UploadFile u where u.board = :board")
    void deleteBulkFileByBoardId(@Param("board") Board board);
}
