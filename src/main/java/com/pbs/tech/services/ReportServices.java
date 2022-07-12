package com.pbs.tech.services;

import com.pbs.tech.model.ReportPeriod;
import com.pbs.tech.repo.ReportPeriodRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;

@Service
public class ReportServices {
    @Autowired
    ReportPeriodRepo reportPeriodRepo;

    Logger log= LoggerFactory.getLogger(ReportServices.class);

    public void saveReportConfigs(ReportPeriod reportPeriod) throws Exception {
        try {
            ReportPeriod reportPeriod1 = reportPeriodRepo.findById(reportPeriod.getId()).get();
            reportPeriod1.setId(reportPeriod.getId());
            reportPeriod1.setReportPeriod(reportPeriod.getReportPeriod());
            reportPeriod1.setMail(reportPeriod.getMail());
            reportPeriod1.setPreviousDate(reportPeriod1.getPreviousDate());
            reportPeriod1.setUpdatedBy("Admin");
            reportPeriod1.setUpdatedDt(new Date());
            reportPeriodRepo.save(reportPeriod1);
        }catch (Exception e){
            throw new Exception("Configurations Not saved " + e.getMessage());
        }
    }

    public ReportPeriod getList(){
        ReportPeriod reportPeriod = null;
        try {
            reportPeriod = reportPeriodRepo.findById(Long.valueOf(0)).get();
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("No Such Element Found " + e.getMessage());
        }
        return reportPeriod;
    }
}
