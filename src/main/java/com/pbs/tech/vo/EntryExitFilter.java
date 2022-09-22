package com.pbs.tech.vo;

import java.util.Date;

public class EntryExitFilter {

    public EntryExitFilter(Date date, String id, String name, String location){
        this.date = date;
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public EntryExitFilter(){}

    private Date date;
    private String id;
    private String name;
    private String location;

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

}
