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

    @PostMapping("/access/getList/{pageNumber}")
    public ResponseEntity<?> getAccessViolationList(@RequestHeader("CLIENT_KEY") String clientKey,
                                                    @RequestBody UnknownFilterVO unknownFilterVO,
                                                    @PathVariable int pageNumber) throws Exception {
        return new ResponseEntity<>(accessViolationServices.getViolationList(unknownFilterVO, pageNumber), HttpStatus.OK);
    }

    @GetMapping("/access/count/{date}")
    public ResponseEntity<?> unknownCount(@RequestHeader("CLIENT_KEY") String clientKey,
                                          @PathVariable String date) throws Exception {
        return new ResponseEntity<>(accessViolationServices.violationCount(date), HttpStatus.OK);
    }

    @PostMapping("/access/getList")
    public ResponseEntity<?> violationList(@RequestHeader("CLIENT_KEY") String clientKey,
                                                    @RequestBody UnknownFilterVO unknownFilterVO) throws Exception {
        return new ResponseEntity<>(accessViolationServices.violationList(unknownFilterVO), HttpStatus.OK);
    }
}
