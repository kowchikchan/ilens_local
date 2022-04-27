package com.pbs.tech.vo;

import java.util.List;

public class IdTraceVO {
    private long id;
    private String name;
    private List<IdTraceDetailsVO> trace;

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<IdTraceDetailsVO> getTrace() { return trace; }

    public void setTrace(List<IdTraceDetailsVO> trace) { this.trace = trace; }
}
