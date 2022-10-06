package com.pbs.tech.api;


import com.pbs.tech.services.FirebaseMessagingServices;
import com.pbs.tech.services.UserService;
import com.pbs.tech.vo.PushNotificationDataVO;
import com.pbs.tech.vo.UserTokenVO;
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
    UserService userService;


    @PostMapping
    public ResponseEntity<?> getData(@RequestHeader("CLIENT_KEY") String clientKey,
                                     @RequestBody PushNotificationDataVO pushNotificationDataVO) throws Exception {

        UserTokenVO userTokenVO = userService.getById(pushNotificationDataVO.getUserId());
        if(userTokenVO != null) {
            firebaseMessagingServices.sendNotification(pushNotificationDataVO, userTokenVO.getToken());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
