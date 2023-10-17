package com.lotus.booking.Service;

import com.lotus.booking.DTO.TranResponse;
import com.lotus.booking.DTO.TransactionRequest;
import com.lotus.booking.Entity.Transaction;
import com.lotus.booking.Entity.User;
import com.lotus.booking.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService{
    @Autowired
    private TransactionRepository transactionRepository;
    @Override
    public Transaction saveTransaction(User user, Transaction transaction){
        Transaction newTrans = new Transaction();
        newTrans.setName(transaction.getName());
        newTrans.setAmount(transaction.getAmount());
        newTrans.setServiceCount(transaction.getServiceCount());
        newTrans.setDate(transaction.getDate());
        newTrans.setUser(user);
        transactionRepository.save(newTrans);
        return transaction;
    }

    @Override
    public List<Transaction> findTransactionByUserIDAndDate(User user, TransactionRequest transactionRequest) {
        Long user_id = user.getId();
        String date = transactionRequest.getDate();
        return transactionRepository.findByUserIdAndDate(user_id, date);

    }

    @Override
    public List<Transaction> findAllById(User user) {
        Long user_id = user.getId();
        return transactionRepository.findAllById(user_id);
    }

    @Override
    public List<TranResponse> customTransactions(User user) {
        Long user_id = user.getId();
        return transactionRepository.customListOfTransactions(user_id);
    }

}
