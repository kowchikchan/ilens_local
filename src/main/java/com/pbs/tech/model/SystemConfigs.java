package com.pbs.tech.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SystemConfigs extends Audit{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private String profileStore;
    private String profileStoreUserName;

    private String profileStorePassword;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProfileStore() {
        return profileStore;
    }

    public void setProfileStore(String profileStore) {
        this.profileStore = profileStore;
    }

    public String getProfileStoreUserName() {
        return profileStoreUserName;
    }

    public void setProfileStoreUserName(String profileStoreUserName) {
        this.profileStoreUserName = profileStoreUserName;
    }

    public String getProfileStorePassword() {
        return profileStorePassword;
    }

    public void setProfileStorePassword(String profileStorePassword) {
        this.profileStorePassword = profileStorePassword;
    }
}
