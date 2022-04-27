package com.pbs.tech.services.util;


import com.pbs.tech.common.exception.ScriptExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecuteUtil {

   static Logger LOG= LoggerFactory.getLogger(ExecuteUtil.class);


    public static String excecuteCmd(Resource scriptFile) throws IOException {
        StringBuilder output=new StringBuilder();
        File script=scriptFile.getFile();
        script.setExecutable(true);
        String s=script.getAbsolutePath();
        Process proc =Runtime.getRuntime().exec(s);
        BufferedReader read = new BufferedReader(new InputStreamReader(
                proc.getInputStream()));
        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        while (read.ready()) {
            output.append(read.readLine());
        }

        return output.toString();
    }

    public static String excecuteCmd(String scriptFile,String ...args) throws IOException,ScriptExecutionException {
        String SCRT_PATH = System.getProperty("SCRIPT_PATH");
        StringBuilder output=new StringBuilder();
        StringBuilder argsList=new StringBuilder();
        for(String arg:args){
            argsList.append(" ");
            argsList.append(arg);

        }
        String scriptStr=SCRT_PATH+File.separator+scriptFile+argsList.toString();
        LOG.info("Execution script :"+ scriptStr);
        File script =  new File(scriptStr);
        script.setExecutable(true);
        Process proc =Runtime.getRuntime().exec(script.getAbsolutePath());

        BufferedReader read = new BufferedReader(new InputStreamReader(
                proc.getInputStream()));
        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        while (read.ready()) {
            output.append(read.readLine());
        }
        if(output.indexOf("-1:")>-1){
            throw new ScriptExecutionException("Command execution failed."+ output);
        }
        LOG.info("CMD OUTPUT :"+output.toString());
        return output.toString();
    }


}

