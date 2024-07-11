package com.skycastle.auction.entities.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skycastle.auction.utils.Utils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@DiscriminatorValue(value=Utils.ACCOUNT_TYPE.Values.SELLER)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"version", "createdAt","lastModifiedAt", "lastModifiedBy",
        "password","secret","authorities","accountNonExpired","accountNonLocked", "credentialsNonExpired",
"createdBy","deleted","enabled","using2FA"})
public  class Seller extends User {

    @Column(name="name")
    private String name;

    @Column(name="nin")
    @Size(min=14, max = 14, message = "Ugandan format national Id number must be 14 characters")
    private String nin;

    @Column(name="identification")
    private String identification;

    @Column(name="is_approved")
    private Boolean isApproved =  false;

    private String contact;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Seller seller = (Seller) o;
        return getId() != null && Objects.equals(getId(), seller.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
