/*
 * Online auctioning system
 *
 * Copyright (c) 2022.  Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
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
import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Transactional
@Data
@Table(name="auction", indexes = {@Index(name="auction_uuid_idx", columnList = "uuid"),
        @Index(name="auction_idx01", columnList = "status,product")})
public class Auction extends BaseEntity{
    private String name;

    @Column(name = "uuid", columnDefinition = "VARCHAR(255)")
    private String uuid = UUID.randomUUID().toString();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(name="product_image", columnDefinition = "TEXT")
    private String productImage;

    @Column(name="seller_name")
    private String sellerName;

    private Long seller;

    @Column(unique = true)
    private Long product;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    @Column(name = "reserve_price")
    private BigDecimal reservePrice = new BigDecimal(0);
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    @Column(name = "total_amount")
    private BigDecimal totalBidAmount = new BigDecimal(0);

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    @Column(name = "selling_price")
    private BigDecimal sellingPrice = new BigDecimal(0);

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    @Column(name = "min_bid_amount")
    private BigDecimal minBidAmount = new BigDecimal(0);

    @Enumerated(EnumType.STRING)
    private Utils.AUCTION_STATUS status= Utils.AUCTION_STATUS.PENDING;

    @Column(name = "max_duration")
    private Integer maxDuration = 10; // max auction session duration;

    @OneToMany(mappedBy="auction")
    private List<Bid> bids;

}