package com.pbs.tech.vo;

public class DataApiVO {
    private long id;
    private String dataApi;
    private String reportApi;
    private String apiToken;

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getDataApi() { return dataApi;}

    public void setDataApi(String dataApi) { this.dataApi = dataApi; }

    public String getReportApi() {return reportApi;}

    public void setReportApi(String reportApi) {this.reportApi = reportApi;}

    public String getApiToken() { return apiToken; }

    public void setApiToken(String apiToken) { this.apiToken = apiToken; }
}
