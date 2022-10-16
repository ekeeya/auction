package com.oddjobs.auction.entities.users;

import com.oddjobs.auction.utils.Utils;
import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

@Entity
@Data
@DiscriminatorValue(value=Utils.ACCOUNT_TYPE.Values.SELLER)
public class Seller extends User {

    private String name;

    @Size(min=14, max = 14, message = "Ugandan format national Id number must be 14 characters")
    private String nin;

    private String Identification;
}
