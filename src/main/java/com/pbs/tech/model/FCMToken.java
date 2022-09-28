package com.pbs.tech.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FCMToken extends Audit{

    public FCMToken(Long id, String token) {
        this.id = id;
        this.token = token;
    }

    public FCMToken(){}

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name="token", columnDefinition="TEXT")
    private String token;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getToken() {return token;}

    public void setToken(String token) {this.token = token;}
}
