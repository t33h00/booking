package com.lotus.booking.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.lotus.booking.Entity.Report;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query(value = "SELECT * FROM Report r WHERE r.user_id =?1 and r.date =?2", nativeQuery = true)
    Report findByUserIdAndDate(Long user_id, String date);
    
}
