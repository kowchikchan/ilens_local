package com.pbs.tech.api;

import com.pbs.tech.services.DataApiServices;
import com.pbs.tech.vo.DataApiVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/dataApi")
public class DataApiRest {

    @Autowired
    DataApiServices dataApiServices;

    @PostMapping("/update")
    public ResponseEntity<?> updateDataApi(@RequestHeader("CLIENT_KEY") String clientKey,
                                           @RequestBody DataApiVO dataApiVO, HttpServletRequest request) {
        dataApiServices.saveDataApiDetails(dataApiVO, request);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/details")
    public ResponseEntity<Object> getDetails(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity<>(dataApiServices.getDataApiDetails(),HttpStatus.OK);
    }
}
