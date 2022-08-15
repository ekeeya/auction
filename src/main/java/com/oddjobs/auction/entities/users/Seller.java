package com.oddjobs.auction.entities.users;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

@Entity
@Data
@DiscriminatorValue("SELLER")
public class Seller extends  BaseUserEntity {

    private String name;

    @Size(min=14, max = 14, message = "Ugandan format national Id number must be 14 characters")
    private String nin;

    private String Identification;
}
