package com.lotus.booking.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class NotificationRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String body;
    private String imageUrl;
    private String Urgency;
    private Map<String, String> data = new HashMap<>();

}
