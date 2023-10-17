package com.lotus.booking.DTO;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class AuthenicationRequest {
    @Email
    private String email;
    private String password;
}
