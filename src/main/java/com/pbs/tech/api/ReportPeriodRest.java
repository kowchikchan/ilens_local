package com.pbs.tech.api;

import com.pbs.tech.model.ReportPeriod;
import com.pbs.tech.services.ReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/report")
public class ReportPeriodRest {

    @Autowired
    ReportServices reportServices;

    @PostMapping("/save")
    public ResponseEntity<Object> saveReportConfig(@RequestHeader("CLIENT_KEY") String clientKey,
                                                     @RequestBody ReportPeriod reportPeriod, HttpServletRequest request) throws Exception {
        reportServices.saveReportConfigs(reportPeriod, request);
        return new ResponseEntity<Object>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getConfigurations(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity<>(reportServices.getList(), HttpStatus.OK);
    }

}
