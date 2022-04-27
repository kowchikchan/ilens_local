package com.pbs.tech.config;

import com.pbs.tech.model.DataApi;
import com.pbs.tech.repo.DataApiRepo;
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

    @Value("${ilens.python.path}")
    String pythonPath;

    @EventListener(ApplicationReadyEvent.class)
    public void addDefaultUser() throws Exception {

        // Default User
        UserVo userVo = new UserVo();
        try {
            userVo.setId(1l);
            userVo.setUserId("Admin");
            userVo.setUserSecret("Admin");
            userVo.setRole("Admin");
            userVo.setFirstName("Default User");
            userVo.setLastName("Default User");
            userVo.setDateOfBirth(new Date());
            userVo.setActive(true);
            userVo.setDepartment("Infra");
            userVo.setLocation("Default");
            userVo.setCreateBy("Dev");
            userVo.setCreatedDt(new Date());
            userVo.setUpdatedBy("Dev");
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
            dataApi.setDataApi("");
            dataApi.setReportApi("");
            dataApi.setApiToken("ilens ~ org.springframework.security" +
                    ".authentication.UsernamePasswordAuthenticationToken@fc1b0f20:" +
                    " Principal: Admin; Credentials: [PROTECTED]");
            dataApiRepo.save(dataApi);
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
