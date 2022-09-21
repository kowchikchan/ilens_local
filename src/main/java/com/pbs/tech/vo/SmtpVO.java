package com.pbs.tech.vo;


public class SmtpVO {

    private String host;
    private long port;
    private boolean ssl;
    private boolean tls;
    private String userMail;
    private String secret;
    private String toMail;

    public String getHost() {return host;}

    public void setHost(String host) {this.host = host;}

    public long getPort() {return port;}

    public void setPort(long port) {this.port = port;}

    public boolean isSsl() {return ssl;}

    public void setSsl(boolean ssl) {this.ssl = ssl;}

    public boolean isTls() {return tls;}

    public void setTls(boolean tls) {this.tls = tls;}

    public String getUserMail() {return userMail;}

    public void setUserMail(String userMail) {this.userMail = userMail;}

    public String getSecret() {return secret;}

    public void setSecret(String secret) {this.secret = secret;}

    public String getToMail() {return toMail;}

    public void setToMail(String toMail) {this.toMail = toMail;}
}
