package com.pbs.tech.vo;

import java.util.List;

public class ReportVO {
    private String totalOnTime;
    private String totalGraceTime;
    private String totalBeyondGraceTime;
    private String onTime;
    private String graceTime;
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
}
