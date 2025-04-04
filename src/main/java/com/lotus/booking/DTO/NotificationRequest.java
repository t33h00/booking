package com.lotus.booking.DTO;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class NotificationRequest {

    private String title;
    private String body;
    private Long user_id;
    private String imageUrl;
    private String Urgency;
    private Map<String, String> data = new HashMap<>();

}
