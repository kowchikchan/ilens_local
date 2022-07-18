package com.pbs.tech.services;

import com.pbs.tech.model.ReportPeriod;
import com.pbs.tech.repo.ReportPeriodRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.NoSuchElementException;

@Service
public class ReportServices {
    @Autowired
    ReportPeriodRepo reportPeriodRepo;

    Logger log= LoggerFactory.getLogger(ReportServices.class);

    public void saveReportConfigs(ReportPeriod reportPeriod, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Object userId = session.getAttribute("USER_ID");
        try {
            ReportPeriod reportPeriod1 = reportPeriodRepo.findById(reportPeriod.getId()).get();
            reportPeriod1.setId(reportPeriod.getId());
            reportPeriod1.setReportPeriod(reportPeriod.getReportPeriod());
            reportPeriod1.setMail(reportPeriod.getMail());
            reportPeriod1.setPreviousDate(reportPeriod1.getPreviousDate());
            reportPeriod1.setUpdatedBy(userId.toString());
            reportPeriod1.setUpdatedDt(new Date());
            reportPeriodRepo.save(reportPeriod1);
        }catch (Exception e){
            throw new Exception("Configurations Not saved " + e.getMessage());
        }
    }

    public ReportPeriod getList(){
        ReportPeriod reportPeriod = null;
        try {
            reportPeriod = reportPeriodRepo.findById(Long.valueOf(1)).get();
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("No Such Element Found " + e.getMessage());
        }
        return reportPeriod;
    }

    public void putConfigs() throws Exception {
        try {
            ReportPeriod rprtPeriod = reportPeriodRepo.findById(Long.valueOf(1)).get();
            rprtPeriod.setPreviousDate(new Date());
            rprtPeriod.setUpdatedDt(new Date());
            reportPeriodRepo.save(rprtPeriod);
        }catch (Exception e){
            throw new Exception("Configurations Not saved " + e.getMessage());
        }
    }
}
