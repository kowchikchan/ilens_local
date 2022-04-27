package com.pbs.tech.vo.runtime;

import java.util.List;

public class EntryViolationVo {
    private String id;
    private String violationDesc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace() {
        return violationDesc;
    }

    public void setPlace(String violationDesc) {
        this.violationDesc = violationDesc;
    }
}
