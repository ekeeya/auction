package com.oddjobs.auction.entities.users;

import com.oddjobs.auction.utils.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Data
@NoArgsConstructor
@DiscriminatorValue(Utils.ACCOUNT_TYPE.Values.ADMIN)
public  class AdminUser extends User {

    @Column(name="firstname")
    private  String firstname;
    @Column(name="lastname")
    private String lastname;
    @Column(name="department")
    private String department;
}
