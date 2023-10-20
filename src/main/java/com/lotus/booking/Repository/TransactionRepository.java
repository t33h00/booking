package com.lotus.booking.Repository;

import com.lotus.booking.DTO.TranDetailResponse;
import com.lotus.booking.DTO.TranResponse;
import com.lotus.booking.Entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    public static final String calendarView = "SELECT SUM(t.amount) as amount, t.date  FROM Transactions t WHERE t.user_id = ?1 GROUP BY t.date";
    @Query(value = "SELECT * FROM Transactions t WHERE t.user_id =?1 and t.date =?2", nativeQuery = true)
    List<Transaction> findByUserIdAndDate(Long user_id, String date);
    @Query(value = "SELECT * FROM Transactions t WHERE t.user_id =?1", nativeQuery = true)
    List<Transaction> findAllById(Long user_id);

    @Query(value=calendarView, nativeQuery = true)
    List<TranResponse> customListOfTransactions(Long user_id);
    @Query(value="SELECT t.id, t.name, t.amount, t.pay_by, t.tip, t.count, t.note, t.date FROM Transactions t WHERE t.user_Id = ?1 AND t.id = ?2", nativeQuery = true)
    TranDetailResponse findTransactionByUserIdAndId(Long user_id, Long id);

}
