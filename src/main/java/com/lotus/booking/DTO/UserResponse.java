package com.lotus.booking.DTO;

import java.time.LocalDateTime;

public interface UserResponse {
    String getFirst_Name();
    String getLast_Name();
    String role();
    Long getId();
    String getEmail();

}
