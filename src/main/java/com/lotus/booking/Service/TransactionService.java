package com.lotus.booking.Service;

import com.lotus.booking.Controllers.TransactionApi;
import com.lotus.booking.DTO.TranDetailResponse;
import com.lotus.booking.DTO.TranResponse;
import com.lotus.booking.DTO.TransactionRequest;
import com.lotus.booking.DTO.TransactionRequestById;
import com.lotus.booking.Entity.Transaction;
import com.lotus.booking.Entity.User;

import java.util.Date;
import java.util.List;

public interface TransactionService {
    Transaction saveTransaction(User user, Transaction transaction);

    List<Transaction> findTransactionByUserIDAndDate(User user, TransactionRequest transactionRequest);

    List<Transaction> findAllById(User user);

    List<TranResponse> customTransactions(User user);

    TranDetailResponse findTransactionByUserIdAndId(User user, TransactionRequestById transactionRequestById);

    Transaction updateTransaction(Transaction transaction);

    String deleteTransaction(TransactionRequestById transactionRequestById);
}
