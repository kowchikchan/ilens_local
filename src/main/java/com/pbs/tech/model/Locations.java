package com.pbs.tech.model;

import com.pbs.tech.common.config.DeviceType;
import javax.persistence.*;

@Entity
public class Locations extends Audit {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(unique=true)
    private String name;

    private DeviceType type;

}
