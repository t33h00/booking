package com.lotus.booking.Repository;

import com.lotus.booking.Entity.Verify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VerifyRepository extends JpaRepository<Verify,Long> {
    @Query(value = "SELECT * FROM verify v WHERE v.user_id =?1 and v.date =?2", nativeQuery = true)
    Verify findByUserIdAndDate(Long user_id, String date);

    @Query(value = "SELECT *  FROM verify v WHERE v.user_id = ?1 AND v.date LIKE ?2",nativeQuery = true)
    List<Verify> getVerifyByMonth(Long id, String date);
}
