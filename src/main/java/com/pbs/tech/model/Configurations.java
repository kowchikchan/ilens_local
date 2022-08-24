package com.pbs.tech.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Configurations extends Audit{
    @Id
    @Column(unique=true)
    private long id;
    private long retainsPeriod;
    @Column(columnDefinition = "boolean default false")
    private boolean videoStatus;
    private String onTime;
    private String graceTime;
    private long gracePeriod;
    private String exitOnTime;
    private String exitGraceTime;
    private long exitGracePeriod;

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public long getRetainsPeriod() {return retainsPeriod;}

    public void setRetainsPeriod(long retainsPeriod) {this.retainsPeriod = retainsPeriod;}

    public boolean isVideoStatus() {return videoStatus;}

    public void setVideoStatus(boolean videoStatus) {this.videoStatus = videoStatus;}

    public String getOnTime() {return onTime;}

    public void setOnTime(String onTime) {this.onTime = onTime;}

    public String getGraceTime() {return graceTime;}

    public void setGraceTime(String graceTime) {this.graceTime = graceTime;}

    public long getGracePeriod() {return gracePeriod;}

    public void setGracePeriod(long gracePeriod) {this.gracePeriod = gracePeriod;}

    public String getExitOnTime() {return exitOnTime;}

    public void setExitOnTime(String exitOnTime) {this.exitOnTime = exitOnTime;}

    public String getExitGraceTime() {return exitGraceTime;}

    public void setExitGraceTime(String exitGraceTime) {this.exitGraceTime = exitGraceTime;}

    public long getExitGracePeriod() {return exitGracePeriod;}

    public void setExitGracePeriod(long exitGracePeriod) {this.exitGracePeriod = exitGracePeriod;}
}
