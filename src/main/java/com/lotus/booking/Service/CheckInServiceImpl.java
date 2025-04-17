package com.lotus.booking.Service;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.lotus.booking.DTO.AllDevicesNotificationRequest;
import com.lotus.booking.Entity.CheckIn;
import com.lotus.booking.Entity.Subscriber;
import com.lotus.booking.Repository.CheckInRepository;
import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        newCheckin.setDate(LocalDateTime.now(zoneId));
        newCheckin.setName(checkIn.getName());
        newCheckin.setPhone(checkIn.getPhone());
        newCheckin.setService(checkIn.getService());
        newCheckin.setAppt(checkIn.getAppt());
        newCheckin.setRequest(checkIn.getRequest());
        checkInRepository.save(newCheckin);
        if(checkIn.getRequest() != null){
            if(checkIn.getService().contains("Facial") && checkIn.getRequest() != 0){
                List<Subscriber> subscribers = subscriberService.findAllSubscriberById(checkIn.getRequest());
                subscribers.addAll(subscriberService.findAllSubscriberById(12L));
                List<String> devices = new ArrayList<>();
                devices.addAll(subscribers.stream().map(Subscriber::getToken).toList());
                AllDevicesNotificationRequest request = sendToRequest(checkIn);
                request.setDeviceTokenList(devices);
                notificationService.sendMulticastNotificationToAll(request);
            } else if (checkIn.getRequest() != 0){
                AllDevicesNotificationRequest request = sendToRequest(checkIn);
                notificationService.sendMulticastNotificationToAll(request);
            }
        }
        return newCheckin.getName();
    }

    private AllDevicesNotificationRequest getAllDevicesNotificationRequest(CheckIn checkIn) {
        List<Subscriber> subscribers = subscriberService.getAllSubscriber();
        List<String> devices = new ArrayList<>();
        for(Subscriber sub : subscribers){
            devices.add(sub.getToken());
        }
        // Combine all key-value pairs into a single map
        Map<String, String> data = new HashMap<>();
        data.put("name", checkIn.getName());
        data.put("service", checkIn.getService());
        data.put("app", checkIn.getAppt());
        data.put("url","https://lotuswages.com");

        AllDevicesNotificationRequest request = new AllDevicesNotificationRequest();
        request.setData(data);
        request.setDeviceTokenList(devices);
        return request;
    }

    private AllDevicesNotificationRequest sendToRequest(CheckIn checkIn){
        List<Subscriber> subscribers = subscriberService.findAllSubscriberById(checkIn.getRequest());
        List<String> devices = new ArrayList<>();
        for(Subscriber sub : subscribers){
            devices.add(sub.getToken());
        }
        Map<String, String> data = new HashMap<>();
        data.put("name", checkIn.getName());
        data.put("service", checkIn.getService());
        data.put("app", checkIn.getAppt());
        AllDevicesNotificationRequest request = new AllDevicesNotificationRequest();
        request.setData(data);
        request.setDeviceTokenList(devices);
        return request;
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
