package tyop.tyop.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tyop.tyop.board.model.Board;
import tyop.tyop.board.model.BoardState;

import java.time.LocalDate;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select b from Board b join b.member m where b.createdDate = :nowDate and b.boardState = NORMAL")
    Page<Board> findHotBoards(@Param("nowDate") LocalDate nowDate, Pageable pageable);

    @Query("select b from Board b join b.member m where b.boardState = NORMAL and b.title like %:title%")
    Page<Board> searchBoardsByTitle(@Param("title") String keyword, Pageable pageable);

    @Query("select b from Board b join b.member m where b.boardState = NORMAL and b.tag like %:tag%")
    Page<Board> searchBoardsByTag(@Param("tag") String keyword, Pageable pageable);

    @Query("select b from Board b join b.member m where m.email = :email")
    Page<Board> findBoardsByEmail(@Param("email") String email, Pageable pageable);

    @Query("select b from Board b join fetch b.member m where b.id = :id")
    Board findOneBoard(@Param("id") Long id);

    @Modifying
    @Query("update Board b set b.hit = b.hit + 1 where b.id = :id")
    void updateHit(@Param("id") Long id);

    @Modifying
    @Query("update Board b set b.boardState = :boardState where b.id = :id")
    void reportBoard(@Param("boardState") BoardState boardState, @Param("id") Long id);
}
