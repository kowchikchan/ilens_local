package com.pbs.tech.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.pbs.tech.vo.PushNotificationDataVO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingServices {


    final FirebaseMessaging firebaseMessaging;

    public FirebaseMessagingServices(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }
    @Async
    public void sendNotification(PushNotificationDataVO dataVO, String token) throws Exception {
        try {
            Notification notification = Notification
                    .builder()
                    .setTitle(dataVO.getSubject())
                    .setBody(dataVO.getContent())
                    .build();

            Message message = Message
                    .builder()
                    .setToken(token)
                    .setNotification(notification)
                    .putAllData(dataVO.getData())
                    .build();
            firebaseMessaging.send(message);

        }catch (Exception e){
            throw new Exception("Exception : " + e.getMessage());
        }
    }

    public void sendTestNotification(PushNotificationDataVO dataVO, String token) throws Exception {
        try {
            Notification notification = Notification
                    .builder()
                    .setTitle(dataVO.getSubject())
                    .setBody(dataVO.getContent())
                    .build();

            Message message = Message
                    .builder()
                    .setToken(token)
                    .setNotification(notification)
                    .putAllData(dataVO.getData())
                    .build();
            firebaseMessaging.send(message);

        }catch (Exception e){
            throw new Exception("Exception : " + e.getMessage());
        }
    }
}
