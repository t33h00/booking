package com.lotus.booking.Service;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.lotus.booking.DTO.AllDevicesNotificationRequest;
import com.lotus.booking.Entity.CheckIn;
import com.lotus.booking.Entity.Subscriber;
import com.lotus.booking.Repository.CheckInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CheckInServiceImpl implements CheckInService {
    @Autowired
    private CheckInRepository checkInRepository;
    @Autowired
    NotificationService notificationService;
    @Autowired
    SubscriberService subscriberService;

    @Override
    public String saveCheckInRecord(CheckIn checkIn) throws ExecutionException, FirebaseMessagingException, InterruptedException {
        ZoneId zoneId = ZoneId.of("US/Eastern");
        CheckIn newCheckin = new CheckIn();
        newCheckin.setDate(LocalDateTime.now());
        newCheckin.setName(checkIn.getName());
        newCheckin.setPhone(checkIn.getPhone());
        newCheckin.setService(checkIn.getService());
        newCheckin.setAppt(checkIn.getAppt());
        newCheckin.setRequest(checkIn.getRequest());
        checkInRepository.save(newCheckin);
        if(checkIn.getService().contains("Face+Wax")){
            List<Subscriber> subscribers = subscriberService.getAllSubscriber();
            List<String> devices = new ArrayList<>();
            for(Subscriber sub : subscribers){
                devices.add(sub.getToken());
            }
            AllDevicesNotificationRequest request = new AllDevicesNotificationRequest();
            request.setTitle(checkIn.getService() + " (" + checkIn.getAppt() + ")");
            request.setBody("Name: " + checkIn.getName());
            request.setDeviceTokenList(devices);
            notificationService.sendMulticastNotification(request);
        }
        return newCheckin.getName();
    }

    @Override
    public List<CheckIn> getAllCheckIn() {
        return checkInRepository.findAll();
    }

    @Override
    public List<CheckIn> findByDate(LocalDateTime date) {
        return checkInRepository.findByDate(date);
    }
    @Override
    public String serve(Long id, boolean serve){
        checkInRepository.serve(id,serve);
        CheckIn checkIn = checkInRepository.findById(id).orElseThrow();
        return String.valueOf(checkIn.isServe());
    }


}
