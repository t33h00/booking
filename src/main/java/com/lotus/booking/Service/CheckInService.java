package com.lotus.booking.Service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.lotus.booking.Entity.CheckIn;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CheckInService {
    public String saveCheckInRecord(CheckIn checkIn) throws FirebaseMessagingException, ExecutionException, InterruptedException;

    public List<CheckIn> getAllCheckIn();

    List<CheckIn> findByDate(Date date);

    String serve(Long id,boolean serve);
}
