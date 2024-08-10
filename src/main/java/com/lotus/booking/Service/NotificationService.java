package com.lotus.booking.Service;


import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import com.lotus.booking.DTO.AllDevicesNotificationRequest;
import com.lotus.booking.DTO.DeviceNotificationRequest;
import com.lotus.booking.DTO.NotificationSubscriptionRequest;
import com.lotus.booking.DTO.TopicNotificationRequest;
import com.lotus.booking.Entity.Subscriber;
import com.lotus.booking.Repository.SubscriberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
@Service
@AllArgsConstructor
@Slf4j
public class NotificationService {
    private final FirebaseApp firebaseApp;

    private final SubscriberRepository subscriberRepository;

    public void sendNotificationToDevice(DeviceNotificationRequest request) throws FirebaseMessagingException, ExecutionException, InterruptedException {
        Message fcmMessage = Message.builder()
                .setToken(request.getDeviceToken())
                .setNotification(
                        Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .setImage(request.getImageUrl())
                                .build()
                ).setAndroidConfig(AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build())
                .setWebpushConfig(WebpushConfig.builder().putHeader("Urgency","high").build())
                .putAllData(request.getData())
                .build();

        String response = FirebaseMessaging.getInstance(firebaseApp).sendAsync(fcmMessage).get();
        log.info("sendNotificationToDevice response: {}", response);
    }

    public void sendPushNotificationToTopic(TopicNotificationRequest request) throws FirebaseMessagingException, ExecutionException, InterruptedException {
        Message fcmMessage = Message.builder()
                .setTopic(request.getTopicName())
                .setNotification(
                        Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .setImage(request.getImageUrl())
                                .build()
                )
                .setAndroidConfig(getAndroidConfig(request.getTopicName()))
                .setApnsConfig(getApnsConfig(request.getTopicName()))
                .putAllData(request.getData())
                .build();

        String response = FirebaseMessaging.getInstance(firebaseApp).sendAsync(fcmMessage).get();
        log.info("sendNotificationToDevice response: {}", response);
    }


    public void sendMulticastNotification(AllDevicesNotificationRequest request) throws FirebaseMessagingException {
        Map<String, String> headers = new HashMap<>();
        headers.put("TTL", "3600");
        headers.put("Urgency", "high");
        WebpushFcmOptions webpushFcmOptions = WebpushFcmOptions.builder().setLink("https://lotuscheckin.web.app/list").build();
        WebpushConfig webpushConfig = WebpushConfig.builder().setFcmOptions(webpushFcmOptions).putAllHeaders(headers).build();

        MulticastMessage multicastMessage = MulticastMessage.builder()
                .addAllTokens(request.getDeviceTokenList().isEmpty() ? getAllDeviceTokens() : request.getDeviceTokenList())
                .setNotification(
                        Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .setImage(request.getImageUrl())
                                .build())
                .setWebpushConfig(webpushConfig)
                .putAllData(request.getData())
                .build();

        BatchResponse response = FirebaseMessaging.getInstance(firebaseApp).sendEachForMulticast(multicastMessage);
        List<SendResponse> responses = response.getResponses();
        List<String> failedToken = new ArrayList<>();
        if(response.getFailureCount()>0){
            for(int i=0;i< responses.size();i++){
                if(!responses.get(i).isSuccessful()){
                    failedToken.add(request.getDeviceTokenList().get(i));
                }
            }
        }
        for (String s : failedToken) {
            Subscriber subscriber =  subscriberRepository.findSubscriberByToken(s);
            Long id = subscriber.getId();
            subscriberRepository.deleteById(id);
        }
    }

    public void subscribeDeviceToTopic(NotificationSubscriptionRequest request) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().subscribeToTopic(
                Collections.singletonList(request.getDeviceToken()),
                request.getTopicName()
        );
    }

    public void unsubscribeDeviceFromTopic(NotificationSubscriptionRequest request) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(
                Collections.singletonList(request.getDeviceToken()),
                request.getTopicName()
        );
    }

    private List<String> getAllDeviceTokens() {
        return new ArrayList<>();
    }

    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(0).toMillis()).setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder().setSound("default")
                        .setColor("#FFFF00").setTag(topic).build()).build();
    }

    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
    }

}
