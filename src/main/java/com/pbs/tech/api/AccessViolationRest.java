package com.pbs.tech.api;

import com.pbs.tech.services.AccessViolationServices;
import com.pbs.tech.vo.UnknownFilterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/violation")
public class AccessViolationRest {
    @Autowired
    AccessViolationServices accessViolationServices;

    @PostMapping("/access/getList")
    public ResponseEntity<?> getAccessViolationList(@RequestHeader("CLIENT_KEY") String clientKey,
                                                    @RequestBody UnknownFilterVO unknownFilterVO){
        return new ResponseEntity<>(accessViolationServices.getViolationList(unknownFilterVO), HttpStatus.OK);
    }
}
