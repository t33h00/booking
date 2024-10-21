package com.lotus.booking.Controllers;

import com.lotus.booking.DTO.*;
import com.lotus.booking.Entity.Transaction;
import com.lotus.booking.Entity.User;
import com.lotus.booking.Service.TransactionService;
import com.lotus.booking.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/api")
public class TransactionApi {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;

    @PostMapping("/transaction")
    public Transaction saveTransaction(@AuthenticationPrincipal User user,@RequestBody Transaction transaction){
        return transactionService.saveTransaction(user, transaction);
    }

    //get transaction details for selected date.
    @GetMapping("/findbydate")
    public List<Transaction> findAllTransactionByDate(@AuthenticationPrincipal User user,TransactionRequest transactionRequest){
        return transactionService.findTransactionByUserIDAndDate(user, transactionRequest);
    }

    @GetMapping("/findbydate/{date}")
    public List<Transaction> findAllTransactionByDateParam(@AuthenticationPrincipal User user,@PathVariable("date") String date){
        return transactionService.findTransactionByUserIDAndDateA(user, date);
    }

    @GetMapping("/findall")
    public List<Transaction> findAllById(@AuthenticationPrincipal User user){
        return transactionService.findAllById(user);
    }

    @GetMapping("/calendarview")
    public List<TranResponse> customView(@AuthenticationPrincipal User user, String date){
        return transactionService.customTransactions(user,date);
    }

    @GetMapping("/transaction")
    public TranDetailResponse findTransactionByUserIdAndId(@AuthenticationPrincipal User user, TransactionRequest id){
        return transactionService.findTransactionByUserIdAndId(user, id);
    }

    @PutMapping("/edittransaction")
    public Transaction updateTransaction(@AuthenticationPrincipal User user, @RequestBody Transaction transaction){
        return transactionService.updateTransaction(transaction);
    }

    @DeleteMapping("/deletetransaction")
    public String deleteTransaction(@AuthenticationPrincipal User user,TransactionRequest id){
        return  transactionService.deleteTransaction(id);
    }

    @GetMapping("/customdate")
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

}
