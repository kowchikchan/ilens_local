package com.pbs.tech.services;


import com.pbs.tech.model.FCMToken;
import com.pbs.tech.repo.FCMRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FCMTokenServices {

    Logger log= LoggerFactory.getLogger(FCMTokenServices.class);

    @Autowired
    FCMRepo fcmRepo;


    public void post(FCMToken fcmToken){
        FCMToken token;
        try {
            token = fcmRepo.findById(fcmToken.getId()).get();
        }catch (Exception e){
            token = new FCMToken();
        }
        token.setId(fcmToken.getId());
        token.setToken(fcmToken.getToken());
        token.setCreatedBy(fcmToken.getId().toString());
        token.setCreatedDt(new Date());
        token.setUpdatedBy(fcmToken.getId().toString());
        token.setUpdatedDt(new Date());

        fcmRepo.save(token);
    }

    public List<FCMToken> getFcmTokens() throws Exception {
        try {
            return (List<FCMToken>) fcmRepo.findAll();
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }
}
