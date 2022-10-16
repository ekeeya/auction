package com.oddjobs.auction.entities.users;

import com.oddjobs.auction.utils.Utils;
import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Data
@DiscriminatorValue(Utils.ACCOUNT_TYPE.Values.ADMIN)
public class AdminUser extends User {

    private  String firstname;
    private String lastname;
    private String Department;
}
