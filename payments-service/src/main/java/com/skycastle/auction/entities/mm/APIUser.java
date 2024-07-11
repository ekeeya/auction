/*
 * Online auctioning system
 * Copyright (C) 2023 - , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>
 *
 * This program is not free software
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 */

package com.skycastle.auction.entities.mm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skycastle.auction.entities.BaseEntity;
import com.skycastle.auction.utils.Utils;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorColumn(name = "provider")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DynamicInsert
@DynamicUpdate
@Transactional
@Data
@Table(name="mm_api_user")

public abstract class APIUser  extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(insertable=false, updatable = false, nullable = false)
    private Utils.PROVIDER provider;

    @Enumerated(EnumType.STRING)
    private Utils.ENV environment;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mm_product_id", referencedColumnName = "id")
    private MobileMoneyProduct product;

    @Column(name="access_token", columnDefinition = "TEXT")
    private String accessToken;

    @Column(name="token_expires_in")
    private int accessTokenExpiresIn = 3600;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "access_token_added")
    @Temporal(TemporalType.TIMESTAMP)
    private Date accessTokenAdded;
}
