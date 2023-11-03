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

    @Query(value="UPDATE Users u SET u.is_enable = 1 WHERE u.id = ?1",nativeQuery = true)
    @Modifying
    public void enable(Long id);
}
