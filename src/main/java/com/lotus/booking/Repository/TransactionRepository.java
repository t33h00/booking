package com.lotus.booking.Repository;

import com.lotus.booking.DTO.TranDetailResponse;
import com.lotus.booking.DTO.TranResponse;
import com.lotus.booking.DTO.TranResponseForDates;
import com.lotus.booking.Entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    public static final String calendarView = "SELECT SUM(t.amount) as amount, t.date  FROM Transactions t WHERE t.user_id = ?1 GROUP BY t.date";
    @Query(value = "SELECT * FROM Transactions t WHERE t.user_id =?1 and t.date =?2 ORDER BY t.id ASC", nativeQuery = true)
    List<Transaction> findByUserIdAndDate(Long user_id, String date);
    @Query(value = "SELECT * FROM Transactions t WHERE t.user_id =?1", nativeQuery = true)
    List<Transaction> findAllById(Long user_id);

    @Query(value=calendarView, nativeQuery = true)
    List<TranResponse> customListOfTransactions(Long user_id);
    @Query(value="SELECT t.id, t.name, t.amount, t.pay_by, t.tip, t.count, t.note, t.date FROM Transactions t WHERE t.user_Id = ?1 AND t.id = ?2", nativeQuery = true)
    TranDetailResponse findTransactionByUserIdAndId(Long user_id, Long id);

    @Query(value="SELECT SUM(t.amount) as amount, SUM(t.count) as count,SUM(t.tip) as tip, t.date FROM Transactions t WHERE t.user_id =?1 AND t.date BETWEEN ?2 AND ?3 GROUP BY t.date ORDER by t.date ASC;",nativeQuery = true)
    List<TranResponseForDates> getTranByCustomDate(Long user_id, String date1, String date2);

    @Query(value="SELECT SUM(t.amount) as amount,SUM(t.tip) as tip, SUM(t.count) as count, t.date FROM Transactions t join Users u on t.user_id = u.id AND t.date BETWEEN ?1 AND ?2 group by t.date ORDER BY t.date ASC",nativeQuery = true)
    List<TranResponseForDates> aCustomReportByDateAndUserId(String date1, String date2);

}
