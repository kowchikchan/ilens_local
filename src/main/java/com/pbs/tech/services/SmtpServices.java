package com.pbs.tech.services;


import com.pbs.tech.common.MailSend;
import com.pbs.tech.model.Smtp;
import com.pbs.tech.repo.SmtpRepo;
import com.pbs.tech.vo.SmtpVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.NoSuchElementException;

@Service
public class SmtpServices {

    @Autowired
    SmtpRepo smtpRepo;

    Logger log= LoggerFactory.getLogger(SmtpServices.class);

    public void save(Smtp smtp, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Object userId = session.getAttribute("USER_ID");
        try {
            Smtp smtpConfigs = smtpRepo.findById(smtp.getId()).get();
            smtpConfigs.setId(smtpConfigs.getId());
            smtpConfigs.setHost(smtp.getHost());
            smtpConfigs.setPort(smtp.getPort());
            smtpConfigs.setSsl(smtp.isSsl());
            smtpConfigs.setTls(smtp.isTls());
            smtpConfigs.setUserMail(smtp.getUserMail());
            smtpConfigs.setSecret(smtp.getSecret());

            //Audit
            smtpConfigs.setCreatedDt(smtpConfigs.getCreatedDt());
            smtpConfigs.setCreatedBy(smtpConfigs.getCreatedBy());
            smtpConfigs.setUpdatedDt(new Date());
            smtpConfigs.setCreatedBy(userId.toString());
            smtpRepo.save(smtpConfigs);
        }catch (Exception e){
            throw new Exception("Configurations Not saved " + e.getMessage());
        }
    }

    public Smtp getList(){
        Smtp smtp = null;
        try {
            smtp = smtpRepo.findById(1L).get();
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("No Records Found " + e.getMessage());
        }
        return smtp;
    }

    public void testMail(SmtpVO smtpVO) throws Exception {
        MailSend.mailSend(smtpVO.isTls(), smtpVO.isSsl(), smtpVO.getHost(), String.valueOf(smtpVO.getPort()), smtpVO.getUserMail(), smtpVO.getSecret(), smtpVO.getToMail(), "ilens - SMTP Test mail", "SMTP configuration test mail.", null);
    }
}
