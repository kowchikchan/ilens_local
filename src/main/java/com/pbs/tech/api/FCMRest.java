package com.pbs.tech.api;

import com.pbs.tech.model.FCMToken;
import com.pbs.tech.services.FCMTokenServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/fcm")
public class FCMRest {

    @Autowired
    FCMTokenServices fcmTokenServices;

    @PostMapping
    public ResponseEntity<?> postToken(@RequestHeader("CLIENT_KEY") String clientKey,
                                     @RequestBody FCMToken fcmToken) throws Exception {
        fcmTokenServices.post(fcmToken);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping
    public ResponseEntity<?> getToken(@RequestHeader("CLIENT_KEY") String clientKey) throws Exception {
        return new ResponseEntity<>(fcmTokenServices.getFcmTokens(), HttpStatus.ACCEPTED);
    }
}
