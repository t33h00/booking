package com.lotus.booking.Repository;

import com.lotus.booking.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    @Query(value = "SELECT * FROM Users u WHERE u.verification_code = ?1",nativeQuery = true)
    public User findByVerificationCode(String code);

    @Query(value="UPDATE Users SET is_enable = TRUE WHERE id = ?1",nativeQuery = true)
    @Modifying
    public void enable(Long id);

    @Query(value="UPDATE users u SET verification_code = ?1 where u.email = ?2",nativeQuery = true)
    @Modifying
    public void updateCode(String code, String email);

    @Query(value="UPDATE users u SET password = ?1 where u.email = ?2",nativeQuery = true)
    @Modifying
    public void updatePassword(String password, String email);
}
