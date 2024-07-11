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


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true, value = {"version", "createdBy",
        "lastModifiedAt", "lastModifiedBy","deleted", "enabled"})
@Table(name="bid", indexes = {@Index(name="bid_auction_id_idx", columnList = "auction_id"),
        @Index(name="bid_buyer_idx", columnList = "buyer")})
public class Bid extends BaseEntity {
    private  Long buyer;
    private BigDecimal amount;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="auction_id", referencedColumnName = "id")
    private Auction auction;

    @JsonIgnore
    public Auction getAuction() {
        return auction;
    }

    @Override
    public String toString() {
        return "Bid{" +
                "buyer=" + buyer +
                ", amount=" + amount +
                ", auction=" + auction.getUuid() +
                '}';
    }
}
