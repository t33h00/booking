package com.lotus.booking.Controllers;

import com.lotus.booking.Entity.CheckIn;
import com.lotus.booking.Service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/api")
public class CheckInApi {
    @Autowired
    private CheckInService checkInService;

    @PostMapping("/checkin")
    public String saveCheckIn(@RequestBody CheckIn checkIn){
        return checkInService.saveCheckInRecord(checkIn);
    }

    @GetMapping("/list")
    public List<CheckIn> finAllCheckIn(){
        return checkInService.getAllCheckIn();
    }

    @GetMapping("/list/date")
    public List<CheckIn> findByDate(@RequestParam("date") @DateTimeFormat(pattern= "yyyy-MM-dd") Date date){
        return checkInService.findByDate(date);
    }
}
