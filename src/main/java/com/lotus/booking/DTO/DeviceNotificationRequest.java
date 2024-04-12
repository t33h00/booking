package com.lotus.booking.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceNotificationRequest extends NotificationRequest{
    @NotBlank
    private String deviceToken;
}
