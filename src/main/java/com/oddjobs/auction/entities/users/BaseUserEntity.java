package com.oddjobs.auction.entities.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oddjobs.auction.entities.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@DiscriminatorColumn(name = "account_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "auth_user")
@DynamicInsert
@DynamicUpdate
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BaseUserEntity extends BaseEntity {

    @Column(name = "account_type", nullable = false, insertable = false, updatable = false)
    private ACCOUNT_TYPE accountType;

    @Column(name="username", nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String password;

    private String email;

    private String address;


    public static enum ACCOUNT_TYPE{
        ADMIN, BUYER, SELLER
    }


}
