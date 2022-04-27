package com.pbs.tech.vo;

import com.pbs.tech.model.big.PeopleCount;
import com.pbs.tech.vo.runtime.EntryExitVo;
import com.pbs.tech.vo.runtime.EntryViolationVo;
import com.pbs.tech.vo.runtime.NprVo;
import com.pbs.tech.vo.runtime.SocialViolationVo;

import java.util.List;

public class ChannelData {

    private long channelId;
    private String channelName;
    private String time;
    private String snapshot;
    private List<EntryExitVo> entryExit;
    private NprVo npr;
    private SocialViolationVo socialViolation;
    private List<EntryViolationVo> entryViolationVos;
    private String type;
    private PeopleCount peopleCount;


    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() { return channelName; }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public List<EntryExitVo> getEntryExit() {
        return entryExit;
    }

    public void setEntryExit(List<EntryExitVo> entryExit) {
        this.entryExit = entryExit;
    }

    public NprVo getNpr() {
        return npr;
    }

    public void setNpr(NprVo npr) {
        this.npr = npr;
    }

    public SocialViolationVo getSocialViolation() {
        return socialViolation;
    }

    public void setSocialViolation(SocialViolationVo socialViolation) {
        this.socialViolation = socialViolation;
    }

    public List<EntryViolationVo> getEntryViolationVos() {
        return entryViolationVos;
    }

    public void setEntryViolationVos(List<EntryViolationVo> entryViolationVos) {
        this.entryViolationVos = entryViolationVos;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PeopleCount getPeopleCount() { return peopleCount; }

    public void setPeopleCount(PeopleCount peopleCount) { this.peopleCount = peopleCount; }
}
