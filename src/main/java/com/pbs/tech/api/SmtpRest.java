package com.pbs.tech.api;

import com.pbs.tech.model.Smtp;
import com.pbs.tech.services.SmtpServices;
import com.pbs.tech.vo.SmtpVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/smtp")
public class SmtpRest {

    @Autowired
    SmtpServices smtpServices;

    @PostMapping("/configs/save")
    public ResponseEntity<Object> saveMenuStatus(@RequestHeader("CLIENT_KEY") String clientKey,
                                                 @RequestBody Smtp smtp,
                                                 HttpServletRequest request) throws Exception {
        smtpServices.save(smtp, request);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/configs/list")
    public ResponseEntity<Object> getList(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity<>(smtpServices.getList(), HttpStatus.OK);
    }

    @PostMapping("/mail/test")
    public ResponseEntity<?> testMail(@RequestHeader("CLIENT_KEY") String clientKey, @RequestBody SmtpVO smtpVO) throws Exception {
        smtpServices.testMail(smtpVO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
