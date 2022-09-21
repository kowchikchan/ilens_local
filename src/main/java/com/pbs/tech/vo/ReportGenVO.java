package com.pbs.tech.vo;


public class ReportGenVO {
    private String id;
    private String name;
    private String entryTime;
    private String entryLocation;
    private String exitTime;
    private String exitLocation;

    private String spentHours;

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getEntryTime() {return entryTime;}

    public void setEntryTime(String entryTime) {this.entryTime = entryTime;}

    public String getEntryLocation() {return entryLocation;}

    public void setEntryLocation(String entryLocation) {this.entryLocation = entryLocation;}

    public String getExitTime() {return exitTime;}

    public void setExitTime(String exitTime) {this.exitTime = exitTime;}

    public String getExitLocation() {return exitLocation;}

    public void setExitLocation(String exitLocation) {this.exitLocation = exitLocation;}

    public String getSpentHours() {return spentHours;}

    public void setSpentHours(String spentHours) {this.spentHours = spentHours;}
}
