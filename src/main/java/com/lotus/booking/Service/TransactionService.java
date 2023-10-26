package com.lotus.booking.Service;

import com.lotus.booking.Controllers.TransactionApi;
import com.lotus.booking.DTO.*;
import com.lotus.booking.Entity.Transaction;
import com.lotus.booking.Entity.User;

import java.util.Date;
import java.util.List;

public interface TransactionService {
    Transaction saveTransaction(User user, Transaction transaction);

    //find trans detail by date
    List<Transaction> findTransactionByUserIDAndDate(User user, TransactionRequest transactionRequest);
    List<Transaction> aFindTransactionByUserIDAndDate(TransactionRequest transactionRequest);

    List<Transaction> findAllById(User user);

    //for calendar view sum amount for each date
    List<TranResponse> customTransactions(User user);
    List<TranResponse> aCustomTransactions(TransactionRequest transactionRequest);

    TranDetailResponse findTransactionByUserIdAndId(User user, TransactionRequestById transactionRequestById);

    Transaction updateTransaction(Transaction transaction);

    String deleteTransaction(TransactionRequestById transactionRequestById);

    List<TranResponseForDates> getAllByCustomDate(User user, CustomDateTranRequest customDateTranRequest);
    List<TranResponseForDates> aGetAllByCustomDate(CustomDateTranRequest customDateTranRequest);
}
