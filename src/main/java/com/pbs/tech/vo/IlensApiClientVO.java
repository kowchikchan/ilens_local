package com.pbs.tech.vo;

public class IlensApiClientVO {
    private String api;
    private String clientKey;
    private String method;
    private Object inputVo;

    public String getApi() {return api;}

    public void setApi(String api) {this.api = api;}

    public String getClientKey() {return clientKey;}

    public void setClientKey(String clientKey) {this.clientKey = clientKey;}

    public String getMethod() {return method;}

    public void setMethod(String method) {this.method = method;}

    public Object getInputVo() {return inputVo;}

    public void setInputVo(Object inputVo) {this.inputVo = inputVo;}
}
