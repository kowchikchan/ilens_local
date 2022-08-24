package com.pbs.tech.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class WeekDays extends Audit{
    @Id
    @Column(unique=true)
    private long id;
    private String weekDays;

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public String getWeekDays() {return weekDays;}

    public void setWeekDays(String weekDays) {this.weekDays = weekDays;}
}
