package com.lotus.booking.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    private Long id;
    private String date;
    private Long user_id;

    @Override
    public String toString() {
        return date ;
    }
}

