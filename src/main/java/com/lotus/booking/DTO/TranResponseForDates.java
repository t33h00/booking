package com.lotus.booking.DTO;

import com.lotus.booking.Entity.User;

import java.math.BigDecimal;

public interface TranResponseForDates {
    Double getAmount();
    String getDate();
    int getCount();
    BigDecimal getTip();
    Long getId();
    String getEmail();
    String getBy();

}
