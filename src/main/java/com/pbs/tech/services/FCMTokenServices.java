package com.pbs.tech.services;


import com.pbs.tech.model.FCMToken;
import com.pbs.tech.repo.FCMRepo;
import com.pbs.tech.vo.PushNotificationDataVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class FCMTokenServices {

    Logger log= LoggerFactory.getLogger(FCMTokenServices.class);

    @Autowired
    FCMRepo fcmRepo;

    @Autowired
    FirebaseMessagingServices firebaseMessagingServices;


    public void save(FCMToken fcmToken, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Object userId = session.getAttribute("USER_ID");
        try {
            // inputs
            String subject = "ILens - Test Notification.";
            Map<String, String> data = new HashMap<>();
            data.put("title", "violation");
            data.put("message", subject);
            data.put("snapshot", "");

            // check valid registration token.
            PushNotificationDataVO dataVO = new PushNotificationDataVO(subject, "", data, "");
            firebaseMessagingServices.sendTestNotification(dataVO, fcmToken.getToken());

            // new token details.
            FCMToken tokenConfig = fcmRepo.findById(1L).get();
            tokenConfig.setId(1L);
            tokenConfig.setToken(fcmToken.getToken());

            //Audit
            tokenConfig.setCreatedDt(tokenConfig.getCreatedDt());
            tokenConfig.setCreatedBy(tokenConfig.getCreatedBy());
            tokenConfig.setUpdatedDt(new Date());
            tokenConfig.setCreatedBy(userId.toString());
            fcmRepo.save(tokenConfig);

        }catch (Exception e){
            throw new Exception("Exception : " + e.getMessage());
        }
    }

    public FCMToken get() throws Exception {
        try {
            return fcmRepo.findById(1L).get();
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }
}
