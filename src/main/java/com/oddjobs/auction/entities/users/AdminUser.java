package com.oddjobs.auction.entities.users;

import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class AdminUser extends  BaseUserEntity{

    private  String firstname;
    private String lastname;
    private String Department;
}
