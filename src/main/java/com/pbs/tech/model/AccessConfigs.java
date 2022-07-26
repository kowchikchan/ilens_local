package com.pbs.tech.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class AccessConfigs extends Audit {
    @Id
    @Column(unique = true)
    @GeneratedValue
    private long id;
    private long channelId;
    private String personId;
    private boolean enabled;

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public long getChannelId() { return channelId; }

    public void setChannelId(long channelId) { this.channelId = channelId;}

    public String getPersonId() { return personId; }

    public void setPersonId(String personId) { this.personId = personId; }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}