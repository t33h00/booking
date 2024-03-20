package com.lotus.booking.Repository;

import com.lotus.booking.Entity.CheckIn;
import com.lotus.booking.Entity.Transaction;
import jakarta.persistence.TemporalType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface CheckInRepository extends JpaRepository<CheckIn,Long> {

    @Query(value = "SELECT * FROM checkin c WHERE DATE(date) =?1 ORDER BY c.id ASC", nativeQuery = true)
    List<CheckIn> findByDate(Date date);
}
