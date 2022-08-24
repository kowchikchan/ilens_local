package com.pbs.tech.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingServices {


    final FirebaseMessaging firebaseMessaging;

    public FirebaseMessagingServices(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }
    @Async
    public void sendNotification(String title, String body, String token) throws Exception {
        try {
            Notification notification = Notification.builder().setTitle(title).setBody(body).build();
            Message message = Message.builder().setToken(token).
                    setNotification(notification)
                    //.putAllData(note.getData())
                    .build();
            firebaseMessaging.send(message);
        }catch (Exception e){
            throw new Exception("Exception : " + e.getMessage());
        }
    }
}
