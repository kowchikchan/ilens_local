package com.pbs.tech.vo;

import java.util.List;

public class IdTraceVO {
    private String id;
    private String name;
    private String hourSpent;

    private List<IdTraceDetailsVO> trace;

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getHourSpent() {return hourSpent;}

    public void setHourSpent(String hourSpent) {this.hourSpent = hourSpent;}

    public List<IdTraceDetailsVO> getTrace() { return trace; }

    public void setTrace(List<IdTraceDetailsVO> trace) { this.trace = trace; }
}
