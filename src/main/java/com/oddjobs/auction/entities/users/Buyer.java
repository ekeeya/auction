package com.oddjobs.auction.entities.users;


import com.oddjobs.auction.utils.Utils;
import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(Utils.ACCOUNT_TYPE.Values.BUYER)
@Data
public class Buyer extends User {

    private  String firstname;
    private String lastname;
    private Utils.Gender gender;


}
