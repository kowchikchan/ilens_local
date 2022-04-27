package com.pbs.tech.vo;

import com.pbs.tech.model.FRConfig;
import com.pbs.tech.model.NprConfig;
public class ChannelVo extends AuditVo {

    private long id;
    private String name;
    private String ip;
    private boolean status;
    private String camType;
    private boolean mappings;
    private boolean isEntry;
    private boolean isExit;
    private boolean isRunning;
    private FrConfigsVo frConfigsVo;
    private NprConfigsVo nprConfigsVo;
    private AccessConfigsVo configsVo;

    private boolean frEnabled;

    private boolean nprEnabled;

    private boolean countsEnabled;

    private boolean accessEnabled;


    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getIp() { return ip; }

    public void setIp(String ip) { this.ip = ip; }

    public boolean isStatus() { return status; }

    public void setStatus(boolean status) { this.status = status; }

    public String getCamType() { return camType; }

    public void setCamType(String camType) { this.camType = camType; }

    public boolean isMappings() { return mappings; }

    public void setMappings(boolean mappings) { this.mappings = mappings; }

    public boolean isEntry() { return isEntry; }

    public void setEntry(boolean entry) { isEntry = entry; }

    public boolean isExit() { return isExit; }

    public void setExit(boolean exit) { isExit = exit; }

    public boolean isRunning() { return isRunning; }

    public void setRunning(boolean running) { isRunning = running; }

    public FrConfigsVo getFrConfigsVo() { return frConfigsVo; }

    public void setFrConfigsVo(FrConfigsVo frConfigsVo) { this.frConfigsVo = frConfigsVo; }

    public NprConfigsVo getNprConfigsVo() { return nprConfigsVo; }

    public void setNprConfigsVo(NprConfigsVo nprConfigsVo) { this.nprConfigsVo = nprConfigsVo; }

    public AccessConfigsVo getConfigsVo() { return configsVo; }

    public void setConfigsVo(AccessConfigsVo configsVo) { this.configsVo = configsVo; }

    public boolean isFrEnabled() { return frEnabled; }

    public void setFrEnabled(boolean frEnabled) { this.frEnabled = frEnabled; }

    public boolean isNprEnabled() { return nprEnabled; }

    public void setNprEnabled(boolean nprEnabled) { this.nprEnabled = nprEnabled; }

    public boolean isCountsEnabled() { return countsEnabled; }

    public void setCountsEnabled(boolean countsEnabled) { this.countsEnabled = countsEnabled; }

    public boolean isAccessEnabled() { return accessEnabled; }

    public void setAccessEnabled(boolean accessEnabled) { this.accessEnabled = accessEnabled; }
}
