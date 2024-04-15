package com.lotus.booking.Repository;

import com.lotus.booking.DTO.TranDetailResponse;
import com.lotus.booking.Entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber,Long> {
    @Query(value="SELECT * FROM subscriber s  where token=?1", nativeQuery = true)
    Subscriber findSubscriberByToken(String token);
}
