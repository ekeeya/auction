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

package com.skycastle.auction.dtos.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skycastle.auction.dtos.AuctionRequestDTO;
import com.skycastle.auction.entities.Auction;
import com.skycastle.auction.entities.AuctionResult;
import com.skycastle.auction.entities.Bid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AuctionResponseDTO extends AuctionRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8001795241473090377L;
    private List<Bid> bids = new ArrayList<>();
    private AuctionResult result;
    private String status;

    private String productImage;
    private String sellerName;
    private Long seller;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal totalBidAmount = new BigDecimal(0);

    private Object vehicle;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal minBidAmount;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal reservePrice = new BigDecimal(0);

    public AuctionResponseDTO(Auction auction){
        setId(auction.getId());
        setSeller(auction.getSeller());
        setSellerName(auction.getSellerName());
        setProductImage(auction.getProductImage());
        setName(auction.getName());
        setProduct(auction.getProduct());
        setStartDate(auction.getStartDate());
        setEndDate(auction.getEndDate());
        setMinBidAmount(auction.getMinBidAmount());
        setStatus(auction.getStatus().toString());
        setSellingPrice(auction.getSellingPrice());
        setReservePrice(auction.getReservePrice());
        setBids(auction.getBids());
        setTotalBidAmount(auction.getTotalBidAmount());
    }
}
