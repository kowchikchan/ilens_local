package com.pbs.tech.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class ChannelRunTime {

    Logger log= LoggerFactory.getLogger(ChannelRunTime.class);
   private String name;

   private long pid;

   private String config;

    private boolean canRun = true;

    public ChannelRunTime(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void stop(){
        canRun=false;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }
}
