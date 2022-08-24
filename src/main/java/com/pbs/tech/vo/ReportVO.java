package com.pbs.tech.vo;

import java.util.List;

public class ReportVO {
    private String totalOnTime;
    private String totalGraceTime;
    private String totalBeyondGraceTime;
    private String onTime;
    private String graceTime;
    private String exitOnTime;
    private String exitGraceTime;
    private long weekDaysCount;

    private List<ReportGen1VO> attendance;

    public String getTotalOnTime() {return totalOnTime;}

    public void setTotalOnTime(String totalOnTime) {this.totalOnTime = totalOnTime;}

    public String getTotalGraceTime() {return totalGraceTime;}

    public void setTotalGraceTime(String totalGraceTime) {this.totalGraceTime = totalGraceTime;}

    public String getTotalBeyondGraceTime() {return totalBeyondGraceTime;}

    public void setTotalBeyondGraceTime(String totalBeyondGraceTime) {this.totalBeyondGraceTime = totalBeyondGraceTime;}

    public String getOnTime() {return onTime;}

    public void setOnTime(String onTime) {this.onTime = onTime;}

    public String getGraceTime() {return graceTime;}

    public void setGraceTime(String graceTime) {this.graceTime = graceTime;}

    public List<ReportGen1VO> getAttendance() {return attendance;}

    public void setAttendance(List<ReportGen1VO> attendance) {this.attendance = attendance;}

    public String getExitOnTime() {return exitOnTime;}

    public void setExitOnTime(String exitOnTime) {this.exitOnTime = exitOnTime;}

    public String getExitGraceTime() {return exitGraceTime;}

    public void setExitGraceTime(String exitGraceTime) {this.exitGraceTime = exitGraceTime;}

    public long getWeekDaysCount() {return weekDaysCount;}

    public void setWeekDaysCount(long weekDaysCount) {this.weekDaysCount = weekDaysCount;}
}
