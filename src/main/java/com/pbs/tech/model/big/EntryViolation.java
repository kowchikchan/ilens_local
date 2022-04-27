package com.pbs.tech.model.big;


import org.apache.catalina.LifecycleState;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import javax.persistence.Entity;
import java.util.Date;

@Table("entryviolation")
public class EntryViolation {


    @PrimaryKeyColumn(name = "time",ordinal = 1,type = PrimaryKeyType.PARTITIONED)
    private Date time ;
    @PrimaryKeyColumn(name="id",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
    private String id;
    @PrimaryKeyColumn(name="location",ordinal = 2,type = PrimaryKeyType.PARTITIONED)
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

