package com.pbs.tech.vo;

import java.util.List;

public class AccessConfigsVo {
    private List<String> membersAdd;
    private List<String> membersRemove;

    public List<String> getMembersAdd() {return membersAdd;}

    public void setMembersAdd(List<String> membersAdd) {this.membersAdd = membersAdd;}

    public List<String> getMembersRemove() {return membersRemove;}

    public void setMembersRemove(List<String> membersRemove) {this.membersRemove = membersRemove;}
}
