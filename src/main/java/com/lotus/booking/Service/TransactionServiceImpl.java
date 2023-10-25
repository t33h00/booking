package com.lotus.booking.Service;

import com.lotus.booking.DTO.*;
import com.lotus.booking.Entity.Transaction;
import com.lotus.booking.Entity.User;
import com.lotus.booking.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
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
        newTrans.setPayBy(transaction.getPayBy());
        newTrans.setTip(transaction.getTip());
        newTrans.setCount(transaction.getCount());
        newTrans.setDate(transaction.getDate());
        newTrans.setNote(transaction.getNote());
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
    public List<TranResponseForDates> getAllByCustomDate(User user, CustomDateTranRequest customDateTranRequest){
        Long user_id = user.getId();
        String date1 = customDateTranRequest.getDate1();
        String date2 = customDateTranRequest.getDate2();
        return transactionRepository.getTranByCustomDate(user_id,date1,date2);
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

    @Override
    public TranDetailResponse findTransactionByUserIdAndId(User user, TransactionRequestById transactionRequestById) {
        Long user_id = user.getId();
        Long id = transactionRequestById.getId();
        return transactionRepository.findTransactionByUserIdAndId(user_id, id);
    }

    public Transaction updateTransaction(Transaction transaction){
        Transaction newTransaction = transactionRepository.findById(transaction.getId()).orElseThrow();
        newTransaction.setName(transaction.getName());
        newTransaction.setPayBy(transaction.getPayBy());
        newTransaction.setAmount(transaction.getAmount());
        newTransaction.setTip(transaction.getTip());
        newTransaction.setCount(transaction.getCount());
        newTransaction.setNote(transaction.getNote());
        transactionRepository.save(newTransaction);
        return newTransaction;
    }

    public String deleteTransaction(TransactionRequestById transactionRequestById){
        Long id = transactionRequestById.getId();
        transactionRepository.deleteById(id);
        return "Deleted!";
    }

}
