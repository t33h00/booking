package com.lotus.booking.Controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.lotus.booking.Entity.CheckIn;
import com.lotus.booking.Entity.Subscriber;
import com.lotus.booking.Service.CheckInService;
import com.lotus.booking.Service.SubscriberService;
import org.hibernate.annotations.Check;
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

    @Autowired
    private SubscriberService subscriberService;

    @PostMapping("/checkin")
    public String saveCheckIn(@RequestBody CheckIn checkIn) throws FirebaseMessagingException {
        return checkInService.saveCheckInRecord(checkIn);
    }

    @PostMapping("/subscriber")
    public Subscriber saveSubscriber(@RequestBody Subscriber subscriber){
        return subscriberService.saveSubscriber(subscriber);
    }

    @GetMapping("/list")
    public List<CheckIn> finAllCheckIn(){
        return checkInService.getAllCheckIn();
    }

    @GetMapping("/list/date")
    public List<CheckIn> findByDate(@RequestParam("date") @DateTimeFormat(pattern= "yyyy-MM-dd") Date date){
        return checkInService.findByDate(date);
    }

    @PutMapping("list/update")
    public String serveStatus(Long id, boolean serve){
        return checkInService.serve(id, serve);
    }
}
