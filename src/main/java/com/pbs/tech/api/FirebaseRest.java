package com.pbs.tech.api;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.pbs.tech.services.FirebaseMessagingServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/firebase")
public class FirebaseRest {

    @Autowired
    FirebaseMessagingServices firebaseMessagingServices;

    @GetMapping
    public ResponseEntity<?> getData(@RequestHeader("CLIENT_KEY") String clientKey) throws FirebaseMessagingException {
        firebaseMessagingServices.sendNotification("iLens - Entry Violation",
                "[lf10011] Balamurugan V Entered in Gate 1",
                "ek0SLY4sRCyLCcSY2qMIEm:APA91bENE_SM1tew5DScRZilto-7LQISay3Y2USoIO22Z2aox343xG0_fhEWESkzQGkevYqohqUZ6SAjKE1Xgrmoj_EYLuTPIEGmoXfTBaW_nJxYzfMRK7wZHqyNNoVyQUqQWEBtrABi");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
