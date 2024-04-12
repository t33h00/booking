package com.lotus.booking.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class AllDevicesNotificationRequest extends NotificationRequest{

    List<String> deviceTokenList = new ArrayList<>();
}
