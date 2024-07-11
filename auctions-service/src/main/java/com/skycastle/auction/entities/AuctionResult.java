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

import com.skycastle.auction.utils.Utils;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "auction_result")
public class AuctionResult extends BaseEntity{

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="auction_id", referencedColumnName = "id")
    private Auction auction;

    private Long winner; // Should be a buyer account

    @Column(name="final_amount")
    private BigDecimal finalAmount;

    @Transient
    @Enumerated(EnumType.STRING)
    private Utils.AUCTION_RESULT_STATUS status;

    @PostLoad
    public void setAuctionStatus(){
        //TODO search security context if user is a buyer set this accordingly
        this.setStatus(Utils.AUCTION_RESULT_STATUS.WON);
    }

}
