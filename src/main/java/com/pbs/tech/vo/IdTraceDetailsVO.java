package com.pbs.tech.vo;


import java.util.Date;

public class IdTraceDetailsVO {

    private Date time;
    private String type;
    private String channelId;
    private String snapshot;

    public Date getTime() { return time; }

    public void setTime(Date time) { this.time = time; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getChannelId() { return channelId; }

    public void setChannelId(String channelId) { this.channelId = channelId; }

    public String getSnapshot() { return snapshot; }

    public void setSnapshot(String snapshot) { this.snapshot = snapshot; }
}
