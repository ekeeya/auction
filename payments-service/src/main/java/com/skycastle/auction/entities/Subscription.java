/*
 * Online auctioning system
 *
 * Copyright (c) 2023.  Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skycastle.auction.utils.Utils;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Transactional
@Data
@Table(name="subscription", indexes = {@Index(name="subscription_idx1", columnList = "user_id")})
public class Subscription extends  BaseEntity{

    @Enumerated(EnumType.STRING)
    private Utils.SUBSCRIPTION_TYPE type = Utils.SUBSCRIPTION_TYPE.BUYER;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    @Column(name = "amount")
    private BigDecimal amount = new BigDecimal(0);

    @Column(name = "user_id")
    private Long userId;

    @Type(PostgreSQLHStoreType.class)
    @Column(columnDefinition = "hstore")
    private Map<String, Object> userDetails = new HashMap<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_date", insertable = false,columnDefinition = "TIMESTAMP NOT NULL DEFAULT NOW()")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Enumerated(EnumType.STRING)
    private Utils.SUBSCRIPTION_STATE state= Utils.SUBSCRIPTION_STATE.PENDING;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_date", insertable = false,columnDefinition = "TIMESTAMP NOT NULL DEFAULT NOW()")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Transient
    private Boolean isActive() {
        return this.getEnabled();
    }



}
