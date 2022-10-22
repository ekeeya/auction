package com.oddjobs.auction.entities.users.dto;

import com.oddjobs.auction.utils.Utils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
public class GenericUserDTO implements Serializable {
    private Long id;
    private String username;
    private String email;
    private String address;
    private Map<String, String> settings;
    private String name;
    private Utils.ACCOUNT_TYPE accountType;
    private String nin;
    private String identification;
    private  String firstname;
    private String lastname;
    private Utils.ROLES role ;
    private Utils.Gender gender;
    private String department;
    private Date createdAt;
}
