package com.pbs.tech.api;

import com.pbs.tech.model.Configurations;
import com.pbs.tech.services.ConfigurationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/configurations")
public class ConfigurationsRest {
    @Autowired
    ConfigurationServices configurationServices;

    @PostMapping("/save")
    public ResponseEntity<Object> postConfigurations(@RequestHeader("CLIENT_KEY") String clientKey,
                                              @RequestBody Configurations configurations) throws Exception {
        configurationServices.saveConfigurations(configurations);
        return new ResponseEntity<Object>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getConfigurations(@RequestHeader("CLIENT_KEY") String clientKey){
        return new ResponseEntity<>(configurationServices.getList(), HttpStatus.OK);
    }
}
