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


import com.skycastle.auction.entities.BaseEntity;
import com.skycastle.auction.utils.Utils;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorColumn(name = "provider")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DynamicInsert
@DynamicUpdate
@Transactional
@Data
@Table(name="mm_product")
public abstract class MobileMoneyProduct extends BaseEntity {

    private String name;

    @Enumerated(EnumType.STRING)
    private Utils.PRODUCT_TYPE productType= Utils.PRODUCT_TYPE.COLLECTIONS;

    @Column(insertable=false, updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private Utils.PROVIDER provider;

    @Column(name="call_back_url")
    private String callBackUrl;

}
