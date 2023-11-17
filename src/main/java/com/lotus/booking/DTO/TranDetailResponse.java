package com.lotus.booking.DTO;

import java.math.BigDecimal;

public interface TranDetailResponse {
    Long getId();
    String getName();
    Double getAmount();
    String getPayBy();
    BigDecimal getTip();
    String getNote();
    String getDate();
    int getCount();
}
