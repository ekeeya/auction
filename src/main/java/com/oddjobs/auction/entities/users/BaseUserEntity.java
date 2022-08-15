package com.oddjobs.auction.entities.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oddjobs.auction.entities.BaseEntity;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@DiscriminatorColumn(name = "account_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "auth_user")
@DynamicInsert
@DynamicUpdate
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@TypeDef(name="hstore", typeClass = PostgreSQLHStoreType.class)
public class BaseUserEntity extends BaseEntity {

    @Column(name = "account_type", nullable = false, insertable = false, updatable = false)
    private ACCOUNT_TYPE accountType;

    @Column(name="username", nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String password;

    private ROLES role = ROLES.USER;

    private String email;

    private String address;

    @Type(type = "hstore")
    @Column(columnDefinition = "hstore")
    private Map<String, String> settings = new HashMap<>();
    public static enum ACCOUNT_TYPE{
        ADMIN, BUYER, SELLER
    }

    public static enum ROLES {
        ADMIN, REPORTS, COMMUNICATIONS, USER
    }

}
