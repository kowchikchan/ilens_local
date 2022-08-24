package com.pbs.tech.api;

import com.pbs.tech.model.WeekDays;
import com.pbs.tech.services.WeekDayServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/weekDays")
public class WeekDaysRest {
    @Autowired
    WeekDayServices weekDayServices;

    @PostMapping("/save")
    public ResponseEntity<Object> postWeekDaysConfigurations(@RequestHeader("CLIENT_KEY") String clientKey,
                                                             @RequestBody WeekDays weekDays, HttpServletRequest request) throws Exception {
        weekDayServices.save(weekDays, request);
        return new ResponseEntity<Object>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getConfigurations(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity<>(weekDayServices.getList(), HttpStatus.OK);
    }

}
