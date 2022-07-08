package com.pbs.tech.services;

import com.pbs.tech.model.Configurations;
import com.pbs.tech.repo.ConfigurationsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;

@Service
public class ConfigurationServices {
    @Autowired
    ConfigurationsRepo configurationsRepo;

    Logger log= LoggerFactory.getLogger(ConfigurationServices.class);

    public void saveConfigurations(Configurations configurations) throws Exception {
        try {
            Configurations configurations1 = configurationsRepo.findById(configurations.getId()).get();
            configurations1.setId(0L);
            configurations1.setRetainsPeriod(configurations.getRetainsPeriod());
            configurations1.setVideoStatus(configurations.isVideoStatus());
            configurations1.setOnTime(configurations.getOnTime());
            configurations1.setGraceTime(configurations.getGraceTime());
            configurations1.setGracePeriod(configurations.getGracePeriod());
            configurations1.setUpdatedBy("Admin");
            configurations1.setUpdatedDt(new Date());
            configurationsRepo.save(configurations1);
        }catch (Exception e){
            throw new Exception("Configurations Not saved " + e.getMessage());
        }
    }

    public Configurations getList(){
        Configurations configurations = null;
        try {
             configurations = configurationsRepo.findById(Long.valueOf(0)).get();
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("No Such Element Found " + e.getMessage());
        }
        return configurations;
    }

}
