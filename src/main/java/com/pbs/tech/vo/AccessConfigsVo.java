package com.pbs.tech.vo;

import java.util.List;

public class AccessConfigsVo {
    private List<Long> membersAdd;
    private List<Long> membersRemove;

    public List<Long> getMembersAdd() {
        return membersAdd;
    }

    public void setMembersAdd(List<Long> membersAdd) {
        this.membersAdd = membersAdd;
    }

    public List<Long> getMembersRemove() {
        return membersRemove;
    }

    public void setMembersRemove(List<Long> membersRemove) {
        this.membersRemove = membersRemove;
    }
}
