package com.lotus.booking.Service;

import com.lotus.booking.DTO.*;
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
        newTrans.setBy(transaction.getBy());
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
    public List<Transaction> findTransactionByUserIDAndDateA(User user, String date) {
        Long user_id = user.getId();
        return transactionRepository.findByUserIdAndDate(user_id, date);
    }

    @Override
    public List<Transaction> aFindTransactionByUserIDAndDate(TransactionRequest transactionRequest) {
        Long user_id = transactionRequest.getId();
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
    public List<TranResponseForDates> aGetAllByCustomDate(CustomDateTranRequest customDateTranRequest) {
        Long user_id = customDateTranRequest.getUser_id();
        String date1 = customDateTranRequest.getDate1();
        String date2 = customDateTranRequest.getDate2();
        return transactionRepository.getTranByCustomDate(user_id,date1,date2);
    }

    @Override
    public List<TranResponseForDates> aCustomReportByDateAndUserId(CustomDateTranRequest customDateTranRequest) {
        String date1 = customDateTranRequest.getDate1();
        String date2 = customDateTranRequest.getDate2();
        return transactionRepository.aCustomReportByDateAndUserId(date1,date2);
    }

    @Override
    public List<Transaction> findAllById(User user) {
        Long user_id = user.getId();
        return transactionRepository.findAllById(user_id);
    }

    @Override
    public List<TranResponse> customTransactions(User user, String date) {
        Long user_id = user.getId();
        return transactionRepository.customListOfTransactions(user_id, date);
    }
    @Override
    public List<TranResponse> aCustomTransactions(TransactionRequest transactionRequest) {
        Long user_id = transactionRequest.getUser_id();
        String date = transactionRequest.getDate();
        return transactionRepository.customListOfTransactions(user_id, date);
    }

    @Override
    public TranDetailResponse findTransactionByUserIdAndId(User user, TransactionRequest transactionRequest) {
        Long user_id = user.getId();
        Long id = transactionRequest.getId();
        return transactionRepository.findTransactionByUserIdAndId(user_id, id);
    }

    public Transaction updateTransaction(Transaction transaction){
        Transaction newTransaction = transactionRepository.findById(transaction.getId()).orElseThrow();
        newTransaction.setName(transaction.getName());
        newTransaction.setBy(transaction.getBy());
        newTransaction.setAmount(transaction.getAmount());
        newTransaction.setTip(transaction.getTip());
        newTransaction.setCount(transaction.getCount());
        newTransaction.setNote(transaction.getNote());
        newTransaction.setDate(transaction.getDate());
        transactionRepository.save(newTransaction);
        return newTransaction;
    }

    public String deleteTransaction(TransactionRequest transactionRequest){
        Long id = transactionRequest.getId();
        transactionRepository.deleteById(id);
        return "Deleted!";
    }

}
