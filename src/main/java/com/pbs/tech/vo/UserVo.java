package com.pbs.tech.vo;

import com.pbs.tech.common.ROLES_TYPE;

import java.util.Date;

public class UserVo extends AuditVo {
    private Long id;
    private String userId;
    private String userSecret;
    private String role;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private boolean active;
    private String department;
    private String location;
    private String sessionToken;

    public UserVo() {
    }

    public UserVo(Long id, String userId, ROLES_TYPE role, String firstName, String lastName, Date dateOfBirth, boolean active, Date createdDt, String createdBy, Date updateDt, String updatedBy, String department, String location) {
        this.id = id;
        this.userId =userId;
        this.role = role.name();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.active = active;
        super.setCreatedDt(createdDt);
        super.setCreateBy(createdBy);
        super.setUpdatedDt(updateDt);
        super.setUpdatedBy(updatedBy);
        this.department=department;
        this.location=location;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserSecret() {
        return userSecret;
    }

    public void setUserSecret(String userSecret) {
        this.userSecret = userSecret;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String deparment) {
        this.department = deparment;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
