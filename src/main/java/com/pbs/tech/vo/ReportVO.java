package com.pbs.tech.vo;

import java.util.List;

public class ReportVO {
    private long onTime;
    private long graceTime;
    private long lateTime;
    private List<EntryExit> entryExitList;

    public long getOnTime() {return onTime;}

    public void setOnTime(long onTime) {this.onTime = onTime;}

    public long getGraceTime() {return graceTime;}

    public void setGraceTime(long graceTime) {this.graceTime = graceTime;}

    public long getLateTime() {return lateTime;}

    public void setLateTime(long lateTime) {this.lateTime = lateTime;}

    public List<EntryExit> getEntryExitList() {return entryExitList;}

    public void setEntryExitList(List<EntryExit> entryExitList) {this.entryExitList = entryExitList;}
}
