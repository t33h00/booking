package com.lotus.booking.Service;
import com.lotus.booking.Entity.CheckIn;
import com.lotus.booking.Repository.CheckInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class CheckInServiceImpl implements CheckInService {
    @Autowired
    private CheckInRepository checkInRepository;
    @Override
    public String saveCheckInRecord(CheckIn checkIn){
        CheckIn newCheckin = new CheckIn();
        newCheckin.setName(checkIn.getName());
        newCheckin.setPhone(checkIn.getPhone());
        newCheckin.setService(checkIn.getService());
        newCheckin.setAppt(checkIn.getAppt());
        newCheckin.setRequest(checkIn.getRequest());
        checkInRepository.save(newCheckin);
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
