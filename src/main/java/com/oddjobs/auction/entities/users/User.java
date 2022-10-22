package com.oddjobs.auction.entities.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oddjobs.auction.entities.BaseEntity;
import com.oddjobs.auction.utils.Utils;
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
public  class User extends BaseEntity {

    @Column(name = "account_type", nullable = false, insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Utils.ACCOUNT_TYPE accountType;

    @Column(name="username", nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private Utils.ROLES role = Utils.ROLES.USER;

    private String email;

    private String address;

    @Column(name="is_expired")
    private boolean isExpired = false;

    private Utils.ACCOUNT_STATUS status = Utils.ACCOUNT_STATUS.ACTIVE;

    @Type(type = "hstore")
    @Column(columnDefinition = "hstore")
    private Map<String, String> settings = new HashMap<>();





}
