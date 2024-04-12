package com.lotus.booking.Service;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.lotus.booking.DTO.DeviceNotificationRequest;
import com.lotus.booking.DTO.NotificationRequest;
import com.lotus.booking.Entity.CheckIn;
import com.lotus.booking.Repository.CheckInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CheckInServiceImpl implements CheckInService {
    @Autowired
    private CheckInRepository checkInRepository;
    @Autowired
    NotificationService notificationService;
    @Override
    public String saveCheckInRecord(CheckIn checkIn) throws ExecutionException, FirebaseMessagingException, InterruptedException {
        CheckIn newCheckin = new CheckIn();
        newCheckin.setName(checkIn.getName());
        newCheckin.setPhone(checkIn.getPhone());
        newCheckin.setService(checkIn.getService());
        newCheckin.setAppt(checkIn.getAppt());
        newCheckin.setRequest(checkIn.getRequest());
        checkInRepository.save(newCheckin);
        if(checkIn.getService().contains("Face + wax")){
            DeviceNotificationRequest request = new DeviceNotificationRequest();
            request.setTitle(checkIn.getService());
            request.setBody(checkIn.getName());
            request.setDeviceToken("eSufGvN3emrSvB8CVwmsdK:APA91bG5Bja4qhYmj7UXHyftPvGZAJR3d3weBT960GVaH6WWESm2Rsx5gXRxSyp3HK6jMdaoUf6o5i8d4X3dAFm9H4OV0tbGDdzZKWKurSvmKuz7sK-XA5pDvk5k_6xeoB0-qN0GemaJ");
            request.setImageUrl("");
            notificationService.sendNotificationToDevice(request);
        }
        return newCheckin.getName();
    }

    @Override
    public List<CheckIn> getAllCheckIn() {
        return checkInRepository.findAll();
    }

    @Override
    public List<CheckIn> findByDate(Date date) {
        return checkInRepository.findByDate(date);
    }
    @Override
    public String serve(Long id, boolean serve){
        checkInRepository.serve(id,serve);
        CheckIn checkIn = checkInRepository.findById(id).orElseThrow();
        return String.valueOf(checkIn.isServe());
    }


}