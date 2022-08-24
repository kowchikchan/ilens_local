package com.pbs.tech.vo;

import java.util.List;

public class ReportGen1VO {
    private String date;
    private List<ReportGenVO> employees;

    public String getDate() {return date;}

    public void setDate(String date) {this.date = date;}

    public List<ReportGenVO> getEmployees() {return employees;}

    public void setEmployees(List<ReportGenVO> employees) {this.employees = employees;}
}
