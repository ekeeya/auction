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

package com.skycastle.auction.entities;

import com.skycastle.auction.dtos.User;
import com.skycastle.auction.utils.Utils.PROVIDER;
import com.skycastle.auction.utils.Utils.TRANSACTION_STATUS;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Transactional
@DiscriminatorColumn(name = "provider")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@Table(name="transaction")
public abstract class PaymentTransaction extends  BaseEntity{

    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name="msisdn")
    private String msisdn;

    @Column(name="user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private TRANSACTION_STATUS status =  TRANSACTION_STATUS.PENDING;

    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(insertable=false, updatable=false)
    private PROVIDER provider;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal amount=new BigDecimal(0);

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subscription_id", referencedColumnName = "id")
    private Subscription subscription;

}
