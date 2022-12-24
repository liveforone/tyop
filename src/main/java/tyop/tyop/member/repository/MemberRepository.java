package tyop.tyop.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tyop.tyop.member.model.Member;
import tyop.tyop.member.model.Role;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

    @Query("select m from Member m where m.nickname = :nickname")
    Member findByNickname(@Param("nickname") String nickname);

    @Modifying
    @Query("update Member m set m.auth = :auth where m.email = :email")
    void blockMember(@Param("auth") Role auth, @Param("email") String email);

    @Modifying
    @Query("update Member m set m.email = :newEmail where m.email = :oldEmail")
    void updateEmail(@Param("oldEmail") String oldEmail, @Param("newEmail") String newEmail);

    @Modifying
    @Query("update Member m set m.password = :password where m.id = :id")
    void updatePassword(@Param("id") Long id, @Param("password") String password);

    @Modifying
    @Query("update Member m set m.nickname = :nickname where m.email = :email")
    void updateNickname(@Param("email") String email, @Param("nickname") String nickname);

    @Modifying
    @Query("update Member m set m.blockCount = m.blockCount + 1 where m.email = :email")
    void plusBlockCount(@Param("email") String email);

    @Modifying
    @Query("update Member m set m.blockCount = m.blockCount - 1 where m.email = :email")
    void minusBlockCount(@Param("email") String email);
}
