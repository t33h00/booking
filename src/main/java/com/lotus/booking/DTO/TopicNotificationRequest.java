package com.lotus.booking.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TopicNotificationRequest extends NotificationRequest{
    @NotBlank
    private String topicName;
}
