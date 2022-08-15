package com.oddjobs.auction.entities.users;


import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("BUYER")
@Data
public class Buyer extends BaseUserEntity{

    private  String firstname;
    private String lastname;

    private Gender gender;

    public static  enum Gender{
        MALE, FEMALE
    }
}
