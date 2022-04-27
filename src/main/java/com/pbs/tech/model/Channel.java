package com.pbs.tech.model;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"ip"})})
public class Channel extends Audit {

    @Id
    @GeneratedValue
    @Column(unique=true)
    private long id;
    private String name;
    @Column(unique = true)
    private String ip;
    @Column(columnDefinition = "boolean default true")
    private boolean status;
    private String config;
    private String camType;
    @Column(columnDefinition = "boolean default false")
    private boolean isEntry;
    @Column(columnDefinition = "boolean default false")
    private boolean isExit;

    @Column(columnDefinition = "boolean default false")
    private boolean frEnabled;
    @Column(columnDefinition = "boolean default false")
    private boolean nprEnabled;
    @Column(columnDefinition = "boolean default false")
    private boolean countsEnabled;
    @Column(columnDefinition = "boolean default false")
    private boolean accessEnabled;


    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getIp() { return ip; }

    public void setIp(String ip) { this.ip = ip; }

    public boolean isStatus() { return status; }

    public void setStatus(boolean status) { this.status = status; }

    public String getConfig() { return config; }

    public void setConfig(String config) { this.config = config;}

    public String getCamType() { return camType; }

    public void setCamType(String camType) { this.camType = camType; }

    public boolean isEntry() { return isEntry; }

    public void setEntry(boolean entry) { isEntry = entry; }

    public boolean isExit() { return isExit; }

    public void setExit(boolean exit) { isExit = exit; }

    public boolean isFrEnabled() { return frEnabled; }

    public void setFrEnabled(boolean frEnabled) { this.frEnabled = frEnabled; }

    public boolean isNprEnabled() { return nprEnabled; }

    public void setNprEnabled(boolean nprEnabled) { this.nprEnabled = nprEnabled; }

    public boolean isCountsEnabled() { return countsEnabled; }

    public void setCountsEnabled(boolean countsEnabled) { this.countsEnabled = countsEnabled;}

    public boolean isAccessEnabled() { return accessEnabled; }

    public void setAccessEnabled(boolean accessEnabled) { this.accessEnabled = accessEnabled; }
}
