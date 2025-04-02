package com.lotus.booking.Service;


import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import com.lotus.booking.DTO.AllDevicesNotificationRequest;
import com.lotus.booking.DTO.DeviceNotificationRequest;
import com.lotus.booking.DTO.NotificationSubscriptionRequest;
import com.lotus.booking.DTO.TopicNotificationRequest;
import com.lotus.booking.Entity.Subscriber;
import com.lotus.booking.Repository.SubscriberRepository;

import jakarta.transaction.Transactional;
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

    @Transactional
    public void sendMulticastNotification(AllDevicesNotificationRequest request) throws FirebaseMessagingException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Urgency", "high");
        AndroidConfig androidConfig = AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build();
        WebpushFcmOptions webpushFcmOptions = WebpushFcmOptions.builder().setLink("https://lotuscheckin.web.app/list").build();
        WebpushConfig webpushConfig = WebpushConfig.builder().putAllHeaders(headers).setFcmOptions(webpushFcmOptions).build();

        List<Subscriber> allSub = subscriberRepository.findAllByUserId(request.getUser_id());
        ArrayList<String> allToken = new ArrayList<>();

        for (Subscriber value : allSub) {
            allToken.add(value.getToken());
        }

        MulticastMessage multicastMessage = MulticastMessage.builder()
                .addAllTokens(allToken)
                .setWebpushConfig(webpushConfig)
                .setNotification(
                        Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .setImage(request.getImageUrl())
                                .build())
                .putAllData(request.getData())
                .setAndroidConfig(androidConfig)
                .build();

        BatchResponse response = FirebaseMessaging.getInstance(firebaseApp).sendEachForMulticast(multicastMessage);
        List<SendResponse> responses = response.getResponses();
        List<String> failedTokens = new ArrayList<>();
        System.out.println("Failed token count: " + response.getFailureCount());

        if (response.getFailureCount() > 0) {
            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    failedTokens.add(allToken.get(i));
                }
            }
        }

        // Batch delete all failed tokens
        if (!failedTokens.isEmpty()) {
            deleteFailedTokens(failedTokens);
        }
    }

    public void deleteFailedTokens(List<String> failedTokens) {
        subscriberRepository.deleteAllByTokenIn(failedTokens);
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
