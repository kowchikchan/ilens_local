package com.pbs.tech.api;

import com.pbs.tech.services.IlenService;
import com.pbs.tech.vo.EntryExitFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationsRest {

    @Autowired
    IlenService ilenService;

    @PostMapping("/list")
    public ResponseEntity<Object> notificationsList(@RequestHeader("CLIENT_KEY") String clientKey,
                                                    @RequestBody EntryExitFilter entryExitFilter) throws Exception {
        return new ResponseEntity<Object>(ilenService.getNotificationsList(entryExitFilter), HttpStatus.ACCEPTED);
    }

}
