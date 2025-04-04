package com.lotus.booking.Controllers;

import com.lotus.booking.DTO.*;
import com.lotus.booking.Entity.Transaction;
import com.lotus.booking.Entity.User;
import com.lotus.booking.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(maxAge = 3600)
public class TransactionApi {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/user/transaction")
    public Transaction saveTransaction(@AuthenticationPrincipal User user,@RequestBody Transaction transaction) {
        return transactionService.saveTransaction(user, transaction);
    }

    //get transaction details for selected date.
    @GetMapping("/user/findbydate")
    public List<Transaction> findAllTransactionByDate(@AuthenticationPrincipal User user,TransactionRequest transactionRequest){
        return transactionService.findTransactionByUserIDAndDate(user, transactionRequest);
    }

    @GetMapping("/user/findbydate/{date}")
    public List<Transaction> findAllTransactionByDateParam(@AuthenticationPrincipal User user,@PathVariable("date") String date){
        return transactionService.findTransactionByUserIDAndDateA(user, date);
    }

    @GetMapping("/user/findall")
    public List<Transaction> findAllById(@AuthenticationPrincipal User user){
        return transactionService.findAllById(user);
    }

    @GetMapping("/user/calendarview")
    public List<TranResponse> customView(@AuthenticationPrincipal User user, String date){
        return transactionService.customTransactions(user,date);
    }

    @GetMapping("/user/transaction")
    public TranDetailResponse findTransactionByUserIdAndId(@AuthenticationPrincipal User user, TransactionRequest id){
        return transactionService.findTransactionByUserIdAndId(user, id);
    }

    @PutMapping("/user/edittransaction")
    public Transaction updateTransaction(@AuthenticationPrincipal User user, @RequestBody Transaction transaction){
        return transactionService.updateTransaction(transaction);
    }

    @DeleteMapping("/user/deletetransaction")
    public String deleteTransaction(@AuthenticationPrincipal User user,TransactionRequest id){
        return  transactionService.deleteTransaction(id);
    }

    @GetMapping("/user/customdate")
    public List<TranResponseForDates> getAllByCustomDate(@AuthenticationPrincipal User user, CustomDateTranRequest customDateTranRequest){
        return transactionService.getAllByCustomDate(user, customDateTranRequest);
    }

    //ADMIN

    //get transaction details for selected date.
    @GetMapping("/admin/findbydate")
    public List<Transaction> aFindAllTransactionByDate(@AuthenticationPrincipal User user,TransactionRequest transactionRequest){
        return transactionService.aFindTransactionByUserIDAndDate(transactionRequest);
    }

    @GetMapping("/admin/calendarview")
    public List<TranResponse> aCustomView(@AuthenticationPrincipal User user,TransactionRequest transactionRequest){
        return transactionService.aCustomTransactions(transactionRequest);
    }

    //for Earning view, get all for each day
    @GetMapping("/admin/customdate")
    public List<TranResponseForDates> aGetAllByCustomDate(@AuthenticationPrincipal User user, CustomDateTranRequest customDateTranRequest){
        return transactionService.aGetAllByCustomDate(customDateTranRequest);
    }

    @GetMapping("/admin/report")
    public List<TranResponseForDates> aCustomReportByDateAndUserId(CustomDateTranRequest customDateTranRequest){
        return transactionService.aCustomReportByDateAndUserId(customDateTranRequest);
    }

    @GetMapping("/admin/transaction")
    public Optional<Transaction> findTransactionByUserIdAndId(@AuthenticationPrincipal User user, Long id){
        return transactionService.findTransactionById(id);
    }

}
