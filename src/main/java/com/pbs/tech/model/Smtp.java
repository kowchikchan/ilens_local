package com.pbs.tech.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Smtp extends Audit{
    public Smtp(long id, String host, long port, boolean ssl, boolean tls, String userMail, String secret){
        this.id = id;
        this.host = host;
        this.port = port;
        this.ssl = ssl;
        this.tls = tls;
        this.userMail = userMail;
        this.secret  = secret;
    }
    public Smtp(){}

    @Id
    @Column(unique=true)
    private long id;
    private String host;
    private long port;
    private boolean ssl;
    private boolean tls;
    private String userMail;
    private String secret;

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

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
}
