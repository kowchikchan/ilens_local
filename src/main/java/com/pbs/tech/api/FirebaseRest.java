package com.pbs.tech.api;

import com.pbs.tech.services.FCMTokenServices;
import com.pbs.tech.services.FirebaseMessagingServices;
import com.pbs.tech.vo.PushNotificationDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/firebase")
public class FirebaseRest {

    @Autowired
    FirebaseMessagingServices firebaseMessagingServices;

    @Autowired
    FCMTokenServices fcmTokenServices;

    @PostMapping
    public ResponseEntity<?> getData(@RequestHeader("CLIENT_KEY") String clientKey,
                                     @RequestBody PushNotificationDataVO pushNotificationDataVO) throws Exception {

        firebaseMessagingServices.sendNotification(pushNotificationDataVO, fcmTokenServices.get().getToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
