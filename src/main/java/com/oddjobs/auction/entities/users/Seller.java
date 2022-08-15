package com.oddjobs.auction.entities.users;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Data
@DiscriminatorValue("seller")
public class Seller extends  BaseUserEntity {

    @Column(nullable = false)
    private String name;

    private String nin;

    private String Identification;
}
