package com.lotus.booking.Repository;

import com.lotus.booking.Entity.Subscriber;
import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface SubscriberRepository extends JpaRepository<Subscriber,Long> {
    @Query(value="SELECT * FROM subscriber s  where token=?1", nativeQuery = true)
    Subscriber findSubscriberByToken(String token);

    void deleteAllByTokenIn(List<String> tokens);

    @Query(value = "SELECT * FROM subscriber s where s.user_id = ?1", nativeQuery = true)
        List<Subscriber> findAllByUserId(Long user_id);
}
