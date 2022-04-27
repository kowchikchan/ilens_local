package com.pbs.tech.model;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"channelId"})})
public class NprConfig extends Audit{
    @Id
    @Column(unique=true)
    private long id;
    @Column(unique = true)
    private long channelId;
    private int minRatio;
    private int maxRatio;
    private int minKernel;
    private int maxKernel;
    private boolean enabled;

    public long getId() { return id; }

    public void setId(long id) {
        this.id = id;
    }

    public long getChannelId() { return channelId; }

    public void setChannelId(long channelId) { this.channelId = channelId; }

    public int getMinRatio() {
        return minRatio;
    }

    public void setMinRatio(int minRatio) {
        this.minRatio = minRatio;
    }

    public int getMaxRatio() {
        return maxRatio;
    }

    public void setMaxRatio(int maxRatio) {
        this.maxRatio = maxRatio;
    }

    public int getMinKernel() {
        return minKernel;
    }

    public void setMinKernel(int minKernel) {
        this.minKernel = minKernel;
    }

    public int getMaxKernel() {
        return maxKernel;
    }

    public void setMaxKernel(int maxKernel) {
        this.maxKernel = maxKernel;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
