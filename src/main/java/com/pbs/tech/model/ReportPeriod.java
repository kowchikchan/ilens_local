package com.pbs.tech.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ReportPeriod extends Audit{

    @Id
    @Column(unique=true)
    private long id;

    private long reportPeriod;

    private String mail;

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public long getReportPeriod() {return reportPeriod;}

    public void setReportPeriod(long reportPeriod) {this.reportPeriod = reportPeriod;}

    public String getMail() {return mail;}

    public void setMail(String mail) {this.mail = mail;}

}
