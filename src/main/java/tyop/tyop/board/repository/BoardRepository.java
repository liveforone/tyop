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

    @Query("select b from Board b join b.member m where b.createdDate = :nowDate and b.boardState = :normalState")
    Page<Board> findHotBoards(@Param("nowDate") LocalDate nowDate, @Param("normalState") BoardState boardState, Pageable pageable);

    @Query("select b from Board b join fetch b.member m where b.id = :id")
    Board findOneBoard(@Param("id") Long id);

    @Modifying
    @Query("update Board b set b.hit = b.hit + 1 where b.id = :id")
    void updateHit(@Param("id") Long id);
}
