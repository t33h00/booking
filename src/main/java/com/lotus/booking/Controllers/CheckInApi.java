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
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/api")
public class CheckInApi {
    @Autowired
    private CheckInService checkInService;

    @Autowired
    private SubscriberService subscriberService;

    private final Map<String, Long> tokenStore = new HashMap<>();

    private final String FORM_URL = "https://docs.google.com/forms/u/0/d/e/1FAIpQLSfSPwGr9un-2Zqh-t2jRybul-zY8CgRPBm1paOGmKwa3daI5w/formResponse";
    private final String FORM_URL_LY = "https://docs.google.com/forms/u/0/d/e/1FAIpQLSdWSn2gPNivqxcd4ZrdLoxr3KABGc8_O9aQmfupRV-HjTmd4Q/formResponse?";

    @PostMapping("/checkin")
    public String saveCheckIn(@RequestBody CheckIn checkIn) throws FirebaseMessagingException, ExecutionException, InterruptedException {
        return checkInService.saveCheckInRecord(checkIn);
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitForm(@RequestBody Map<String, String> formData) {
        RestTemplate restTemplate = new RestTemplate();

        // Build query parameters
        StringBuilder queryParams = new StringBuilder("?");
        formData.forEach((key, value) -> queryParams.append(key).append("=").append(value).append("&"));

        // Remove trailing "&"
        String finalUrl = FORM_URL + queryParams.substring(0, queryParams.length() - 1);

        try {
            // Send GET request to Google Forms
            restTemplate.getForObject(finalUrl, String.class);
            return ResponseEntity.ok("Form submitted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error submitting form: " + e.getMessage());
        }
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
    public List<CheckIn> findByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date){
        return checkInService.findByDate(date);
    }

    @PutMapping("list/update")
    public String serveStatus(Long id, boolean serve){
        return checkInService.serve(id, serve);
    }

}
