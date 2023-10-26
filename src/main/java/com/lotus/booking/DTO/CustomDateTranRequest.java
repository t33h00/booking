package com.lotus.booking.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomDateTranRequest {
    private Long user_id;
    private String date1;
    private String date2;
}
