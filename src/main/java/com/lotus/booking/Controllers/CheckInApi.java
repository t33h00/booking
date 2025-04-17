package com.lotus.booking.Controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.lotus.booking.Entity.CheckIn;
import com.lotus.booking.Entity.Subscriber;
import com.lotus.booking.Service.CheckInService;
import com.lotus.booking.Service.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.time.Instant;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/api")
public class CheckInApi {
    @Autowired
    private CheckInService checkInService;

    @Autowired
    private SubscriberService subscriberService;

    private final Map<String, Long> tokenStore = new HashMap<>();

    @PostMapping("/checkin")
    public String saveCheckIn(@RequestBody CheckIn checkIn) throws FirebaseMessagingException, ExecutionException, InterruptedException {
        return checkInService.saveCheckInRecord(checkIn);
    }

    // @GetMapping("/generate-link")
    // public ResponseEntity<Map<String, String>> generateLink() {
    //     String baseURL = "https://api.lotuswages.com";
    //     String token = UUID.randomUUID().toString();
    //     long expirationTime = Instant.now().getEpochSecond() + 300; // 5 minutes from now
    //     tokenStore.put(token, expirationTime);

    //     String link = baseURL + "/api/validate-link?token=" + token;
    //     System.out.println(link);
    //     Map<String, String> response = new HashMap<>();
    //     response.put("link", link);
    //     return ResponseEntity.ok(response);
    // }

    // @GetMapping("/validate-link")
    // public ResponseEntity<Void> validateLink(@RequestParam String token) {
    //     Long expirationTime = tokenStore.get(token);

    //     if (expirationTime == null || Instant.now().getEpochSecond() > expirationTime) {
    //         return ResponseEntity.status(302) // HTTP 302 Found
    //                 .header("Location", "/expired") // Redirect to /expired
    //                 .build();
    //     }

    //     return ResponseEntity.status(302) // HTTP 302 Found
    //             .header("Location", "/checkin") // Redirect to /checkin
    //             .build();
    // }

    @PostMapping("/subscriber")
    public Subscriber saveSubscriber(@RequestBody Subscriber subscriber){
        return subscriberService.saveSubscriber(subscriber);
    }

    @GetMapping("/list")
    public List<CheckIn> finAllCheckIn(){
        return checkInService.getAllCheckIn();
    }

    @GetMapping("/list/date")
    public List<CheckIn> findByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date){
        return checkInService.findByDate(date);
    }

    @PutMapping("list/update")
    public String serveStatus(Long id, boolean serve){
        return checkInService.serve(id, serve);
    }

}
