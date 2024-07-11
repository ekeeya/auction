package com.skycastle.auction.entities.users;

import com.skycastle.auction.utils.Utils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

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
