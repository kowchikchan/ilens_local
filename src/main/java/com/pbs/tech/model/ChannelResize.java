package com.pbs.tech.model;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"channelId"})})
public class ChannelResize {
    @Id
    @GeneratedValue
    @Column(unique=true)
    private long id;
    @Column(unique = true)
    private long channelId;
    private long cLeft;
    private long cTop;
    private long cWidth;
    private long cHeight;
    private long tWidth;
    private long tHeight;

    public ChannelResize(long id, long channelId, long cLeft, long cTop, long cWidth, long cHeight, long tWidth, long tHeight) {
        this.id = id;
        this.channelId = channelId;
        this.cLeft = cLeft;
        this.cTop = cTop;
        this.cWidth = cWidth;
        this.cHeight = cHeight;
        this.tWidth = tWidth;
        this.tHeight = tHeight;
    }
    public ChannelResize(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public long getcLeft() {
        return cLeft;
    }

    public void setcLeft(long cLeft) {
        this.cLeft = cLeft;
    }

    public long getcTop() {
        return cTop;
    }

    public void setcTop(long cTop) {
        this.cTop = cTop;
    }

    public long getcWidth() {
        return cWidth;
    }

    public void setcWidth(long cWidth) {
        this.cWidth = cWidth;
    }

    public long getcHeight() {
        return cHeight;
    }

    public void setcHeight(long cHeight) {
        this.cHeight = cHeight;
    }

    public long gettWidth() {
        return tWidth;
    }

    public void settWidth(long tWidth) {
        this.tWidth = tWidth;
    }

    public long gettHeight() {
        return tHeight;
    }

    public void settHeight(long iHeight) {
        this.tHeight = iHeight;
    }
}
