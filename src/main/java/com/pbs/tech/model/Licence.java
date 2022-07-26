package com.pbs.tech.model;

/**
 * @author Balamurugan V
 * @project ilens
 * @created 22/07/2022
 **/

import javax.persistence.*;

@Entity
public class Licence extends Audit{

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    private String clientName;
    private String serverCount;
    private String startDate;
    private String validTo;
    private String type;
    @Column(name="licenceStr", columnDefinition="TEXT")
    private String licenceStr;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getClientName() {return clientName;}

    public void setClientName(String clientName) {this.clientName = clientName;}

    public String getServerCount() {return serverCount;}

    public void setServerCount(String serverCount) {this.serverCount = serverCount;}

    public String getStartDate() {return startDate;}

    public void setStartDate(String startDate) {this.startDate = startDate;}

    public String getValidTo() {return validTo;}

    public void setValidTo(String validTo) {this.validTo = validTo;}

    public String getType() {return type;}

    public void setType(String type) {this.type = type;}

    public String getLicenceStr() {return licenceStr;}

    public void setLicenceStr(String licenceStr) {this.licenceStr = licenceStr;}
}
