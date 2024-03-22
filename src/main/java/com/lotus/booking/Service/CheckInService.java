package com.lotus.booking.Service;

import com.lotus.booking.Entity.CheckIn;

import java.util.Date;
import java.util.List;

public interface CheckInService {
    public String saveCheckInRecord(CheckIn checkIn);

    public List<CheckIn> getAllCheckIn();

    List<CheckIn> findByDate(Date date);

    String serve(Long id,boolean serve);
}
