package com.skycastle.auction.entities.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skycastle.auction.entities.BaseEntity;
import com.skycastle.auction.utils.Utils;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorColumn(name = "account_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "auth_user")
@DynamicInsert
@DynamicUpdate
@Transactional
@JsonIgnoreProperties(ignoreUnknown = true,value = {"version", "createdAt","lastModifiedAt", "lastModifiedBy", "password", "createdBy"})
@Data

public abstract class User extends BaseEntity implements UserDetails {

    @Column(name = "account_type", nullable = false, insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Utils.ACCOUNT_TYPE accountType;

    @Column(name="username", nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name="role")
    @Enumerated(EnumType.STRING)
    private Utils.ROLES role = Utils.ROLES.ROLE_PRE_VERIFIED;

    private String email;

    private String address;

    @Type(PostgreSQLHStoreType.class)
    @Column(columnDefinition = "hstore")
    private Map<String, String> settings = new HashMap<>();


    private boolean using2FA;

    private String secret;

    @Column(name="is_expired", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isExpired = false;

    @Enumerated(EnumType.STRING)
    private Utils.ACCOUNT_STATUS status = Utils.ACCOUNT_STATUS.ACTIVE;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(String.valueOf(role)));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.isExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.getEnabled();
    }
}
