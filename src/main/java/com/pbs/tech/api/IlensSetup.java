package com.pbs.tech.api;

import com.pbs.tech.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/setup")
public class IlensSetup {

    @Autowired
    UserService userService;

    @PostMapping("/adminUser/{password}")
    public ResponseEntity<?> setUpAdmin(@RequestHeader("CLIENT_KEY") String clientKey, @PathVariable String password){
        userService.addAdminUser(password);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
