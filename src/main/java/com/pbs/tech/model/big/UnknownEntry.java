package com.pbs.tech.model.big;


import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Table("unknownentries")
public class UnknownEntry {


    @PrimaryKeyColumn(name = "time",ordinal = 2,type = PrimaryKeyType.PARTITIONED)
    private Date time ;
    @PrimaryKeyColumn(name="location",ordinal = 1,type = PrimaryKeyType.PARTITIONED)
    private String location;

    private String type;

    private String name;

    private String snapshot;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSnapshot() { return snapshot; }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public String getType() { return type; }

    public void setType(String type) {this.type = type; }
}

