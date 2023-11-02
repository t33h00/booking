package com.lotus.booking.DTO;

import com.lotus.booking.Entity.User;

public interface TranResponseForDates {
    Double getAmount();
    String getDate();
    int getCount();
    int getTip();

    Long getId();
    String getFirst_Name();
//    String getLast_Name();
    String getEmail();
}
