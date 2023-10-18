package com.lotus.booking.Controllers;

import com.lotus.booking.DTO.TranResponse;
import com.lotus.booking.DTO.TransactionRequest;
import com.lotus.booking.Entity.Transaction;
import com.lotus.booking.Entity.User;
import com.lotus.booking.Service.TransactionService;
import com.lotus.booking.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(exposedHeaders = {"Authorization","Access-Control-Allow-Origin","Access-Control-Allow-Credentials"})
@RequestMapping("/api")
public class TransactionApi {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;
    @CrossOrigin(origins = {"https://lotus-ui.web.app", "http://localhost:3000"})
    @PostMapping("/transaction")
    public Transaction saveTransaction(@AuthenticationPrincipal User user,@RequestBody Transaction transaction){
        return transactionService.saveTransaction(user, transaction);
    }
    @CrossOrigin(origins = {"https://lotus-ui.web.app", "http://localhost:3000"})
    @GetMapping("/findbydate")
    public List<Transaction> findAllTransactionByDate(@AuthenticationPrincipal User user,TransactionRequest transactionRequest){
        return transactionService.findTransactionByUserIDAndDate(user, transactionRequest);
    }
    @CrossOrigin(origins = {"https://lotus-ui.web.app", "http://localhost:3000"})
    @GetMapping("/findall")
    public List<Transaction> findAllById(@AuthenticationPrincipal User user){
        return transactionService.findAllById(user);
    }
    @CrossOrigin(origins = {"https://lotus-ui.web.app", "http://localhost:3000"})
    @GetMapping("/calendarview")
    public List<TranResponse> customView(@AuthenticationPrincipal User user){
        return transactionService.customTransactions(user);
    }

}
