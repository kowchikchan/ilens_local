package com.pbs.tech.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MenuStatus {
    @Id
    @Column(unique=true)
    private long id;
    private boolean status;

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public boolean isStatus() {return status;}

    public void setStatus(boolean status) {this.status = status;}
}
