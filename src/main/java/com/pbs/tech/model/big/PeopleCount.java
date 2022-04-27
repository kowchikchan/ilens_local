package com.pbs.tech.model.big;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Table("peoplecount")
public class PeopleCount {

    @PrimaryKeyColumn(name="channelid",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
    private String id;

    @PrimaryKeyColumn(name = "time",ordinal = 1,type = PrimaryKeyType.PARTITIONED)
    private Date time ;

    private String channelName;

    private int count;

    private String violation;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Date getTime() { return time; }

    public void setTime(Date time) { this.time = time; }

    public String getChannelName() { return channelName; }

    public void setChannelName(String channelName) { this.channelName = channelName; }

    public int getCount() { return count; }

    public void setCount(int count) { this.count = count; }

    public String getViolation() { return violation; }

    public void setViolation(String violation) { this.violation = violation; }
}
