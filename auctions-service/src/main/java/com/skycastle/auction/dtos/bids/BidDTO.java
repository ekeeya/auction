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

package com.skycastle.auction.dtos.bids;

import com.skycastle.auction.entities.Bid;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class BidDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8001795241473090377L;
    private BigDecimal amount;

    private Map<String, Object> buyer;

    private Object auction;

    public BidDTO(Bid bid){
        this.setAmount(bid.getAmount());
        Map<String, Object> buyer = new HashMap<>();
        buyer.put("id", bid.getBuyer());
        this.setBuyer(buyer);
    }
}
