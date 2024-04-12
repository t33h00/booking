package com.lotus.booking.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationSubscriptionRequest {
    @NotBlank
    private String deviceToken;
    @NotBlank
    private String topicName;
}
