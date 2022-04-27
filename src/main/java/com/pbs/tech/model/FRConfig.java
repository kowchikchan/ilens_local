package com.pbs.tech.model;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"channelId"})})
public class FRConfig extends Audit{

    @Id
    @Column(unique=true)
    private long id;
    @Column(unique = true)
    private long channelId;
    private int minRatio;
    private int maxRatio;
    private boolean enabled;
    private int minimumPeople;
    private int maximumPeople;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getChannelId() { return channelId; }

    public void setChannelId(long channelId) { this.channelId = channelId; }

    public int getMinimumPeople() {
        return minimumPeople;
    }

    public void setMinimumPeople(int minimumPeople) {
        this.minimumPeople = minimumPeople;
    }

    public int getMaximumPeople() {
        return maximumPeople;
    }

    public void setMaximumPeople(int maximumPeople) {
        this.maximumPeople = maximumPeople;
    }
}
