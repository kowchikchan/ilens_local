package com.pbs.tech.model;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class Audit  implements Serializable {

    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    @CreatedDate
    private Date createdDt;
    @LastModifiedDate
    private Date updatedDt;

    public Audit(){

    }

    public Audit(String createdBy, String updatedBy, Date createdDt, Date updatedDt) {
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdDt = createdDt;
        this.updatedDt = updatedDt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public Date getUpdatedDt() {
        return updatedDt;
    }

    public void setUpdatedDt(Date updatedDt) {
        this.updatedDt = updatedDt;
    }
}
