package com.pbs.tech.config;

import com.pbs.tech.model.*;
import com.pbs.tech.repo.*;
import com.pbs.tech.services.UserService;
import com.pbs.tech.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.NoSuchElementException;


@Configuration
public class SystemConfig {

    private final Logger LOG = LoggerFactory.getLogger(SystemConfig.class);

    @Autowired
    UserService userService;

    @Autowired
    DataApiRepo dataApiRepo;

    @Autowired
    ConfigurationsRepo configurationsRepo;

    @Autowired
    ReportPeriodRepo reportPeriodRepo;

    @Autowired
    MenuStatusRepo menuStatusRepo;

    @Autowired
    SmtpRepo smtpRepo;

    @Autowired
    FCMRepo fcmRepo;

    @Value("${ilens.python.path}")
    String pythonPath;

    @Value("${default-config.user}")
    String user;

    @Value("${default-config.password}")
    String password;

    @Value("${mail.host}")
    String host;

    @Value("${mail.port}")
    long port;

    @Value("${mail.tls}")
    boolean tls;

    @Value("${mail.ssl}")
    boolean ssl;

    @Value("${mail.user-name}")
    String userName;

    @Value("${mail.password}")
    String mailSecret;


    @EventListener(ApplicationReadyEvent.class)
    public void addDefaultUser() throws Exception {

        // Default User
        UserVo userVo = new UserVo();
        try {
            userVo.setId(1l);
            userVo.setUserId(user);
            userVo.setUserSecret(password);
            userVo.setRole("Admin");
            userVo.setFirstName("Default User");
            userVo.setLastName("Default User");
            userVo.setDateOfBirth(new Date());
            userVo.setActive(true);
            userVo.setDepartment("Infra");
            userVo.setLocation("Default");
            userVo.setCreateBy(user);
            userVo.setCreatedDt(new Date());
            userVo.setUpdatedBy(user);
            userVo.setUpdatedDt(new Date());

            userService.addUser(userVo);
        }catch (Exception e){
            LOG.info("Ignorable Exception, User Already Exists");
        }

        // Default DataApi and Api Token
        DataApi dataApi;
        try {
            dataApi = dataApiRepo.findById(0L).get();
        }catch (NoSuchElementException e){
            dataApi = new DataApi();
            dataApi.setDataApi("http://127.0.0.1:61613");
            dataApi.setReportApi("https://127.0.0.1:8088");
            dataApi.setApiToken("PIXw7I9ErRjWsiKmt71fLUJvmZVKiypYXgEDbKJYG7MsjC96p2dWK0A5QbY5Q/IUDo6AIrbEer7zl8IIkrJZSZ");
            dataApiRepo.save(dataApi);
        }

        // Default Configurations.
        Configurations configurations;
        try {
            configurations = configurationsRepo.findById(0L).get();
        }catch (NoSuchElementException e){
            configurations = new Configurations();
            configurations.setId(0L);
            configurations.setRetainsPeriod(30);
            configurations.setVideoStatus(false);
            configurations.setOnTime("9:0 AM");
            configurations.setGraceTime("9:10 AM");
            configurations.setGracePeriod(10);
            configurations.setExitOnTime("6:0 PM");
            configurations.setExitGraceTime("5:50 PM");
            configurations.setExitGracePeriod(10);
            configurations.setCreatedBy(user);
            configurations.setCreatedDt(new Date());
            configurationsRepo.save(configurations);
        }

        // Default Report API Data.
        ReportPeriod reportPeriod;
        try {
            reportPeriod = reportPeriodRepo.findById(1L).get();
        }catch (NoSuchElementException e){
            reportPeriod = new ReportPeriod();
            reportPeriod.setId(1L);
            reportPeriod.setReportPeriod(1);
            reportPeriod.setMail("ilens.logicfocus@logicfocus.com");
            reportPeriod.setPreviousDate(new Date());
            reportPeriod.setWeekDays("monday,tuesday,wednesday,thursday,friday");
            reportPeriod.setCreatedBy(user);
            reportPeriod.setCreatedDt(new Date());
            reportPeriod.setUpdatedBy(user);
            reportPeriod.setUpdatedDt(new Date());
            reportPeriodRepo.save(reportPeriod);
        }

        // save menu status.
        MenuStatus menuStatus;
        try {
            menuStatus = menuStatusRepo.findById(1L).get();
        }catch (NoSuchElementException e){
            menuStatus = new MenuStatus();
            menuStatus.setId(1L);
            menuStatus.setStatus(false);
            menuStatusRepo.save(menuStatus);
        }

        //Default smtp configurations.
        Smtp smtp;
        try {
            smtp = smtpRepo.findById(1L).get();
        }catch (Exception e){
            smtp = new Smtp(1, host, port, ssl, tls, userName, mailSecret);
            smtp.setCreatedBy(user);
            smtp.setUpdatedBy(user);
            smtp.setCreatedDt(new Date());
            smtp.setUpdatedDt(new Date());
            smtpRepo.save(smtp);
        }

        // Start RTSP Server.
        try {
            String error = null;
            String scriptPath = System.getProperty("SCRIPT_PATH");

            //script executing command.
            String executeCmd = pythonPath + " " + scriptPath + "/rtspServer/server.py";
            Process p = Runtime.getRuntime().exec(executeCmd);
            LOG.info("RTSP Server Started With Process ID, {}", p.pid());

            // read output.
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                throw new InterruptedException("Error In Starting RTSP Server, " + e.getMessage());
            }
            while (in.ready()) {
                LOG.info("Output : {}", in.readLine());
            }

            // read, if error occurred.
            BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((error = stderr.readLine()) != null) {
                LOG.error("Error : {}", error);
            }
            stderr.close();
        }catch (IOException e){
            throw new IOException("Error In Starting RTSP Server, " + e.getMessage());
        }

    }
}
