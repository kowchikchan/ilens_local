package com.pbs.tech.vo;

import java.util.List;

public class ChannelRuntimeVo {

    private String api;
    private String ip;
    private String id;
    private List<Execution> executions;
    private String name;


    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Execution> getExecutions() {
        return executions;
    }

    public void setExecutions(List<Execution> executions) {
        this.executions = executions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
