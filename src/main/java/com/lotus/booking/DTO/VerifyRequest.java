package com.lotus.booking.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyRequest {
    private Long id;
    private String date;
    private boolean verified;
    private Long user_id;
}
