package com.lotus.booking.Controllers;

import com.lotus.booking.DTO.VerifyRequest;
import com.lotus.booking.Entity.Report;
import com.lotus.booking.Entity.Verify;
import com.lotus.booking.Entity.User;
import com.lotus.booking.Service.VerifyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(maxAge = 3600)
public class VerifyApi {
    @Autowired
    VerifyServiceImpl verifyService;

    @PostMapping("/admin/verify")
    public void saveVerify(@AuthenticationPrincipal User user, @RequestBody VerifyRequest verify){
        verifyService.saveVerify(verify);
    }

    @GetMapping("/admin/verify")
    public Verify getVerify(@AuthenticationPrincipal User user,Long id,String date) {
        return verifyService.getVerify(id,date);
    }

    @GetMapping("/admin/verifymonth")
    public List<Verify> getVerifyByMonth(@AuthenticationPrincipal User user, Long user_id, String date){
        return verifyService.getVerifyByMonth(user_id,date);
    }
}
