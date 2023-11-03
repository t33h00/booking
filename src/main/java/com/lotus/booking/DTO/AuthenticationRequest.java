package com.lotus.booking.DTO;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class AuthenticationRequest {
    @Email
    private String email;
    private String password;
}
