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

package com.skycastle.auction.mappers;

import com.skycastle.auction.entities.Auction;
import com.skycastle.auction.dtos.responses.AuctionResponseDTO;
import com.skycastle.auction.entities.Bid;
import com.skycastle.auction.services.AuctionResultService;
import com.skycastle.auction.services.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BidderMapper {
    private final BidService bidService;
    private final AuctionResultService auctionResultService;
    public AuctionResponseDTO toAuctionResponseDTO(Auction auction){
        AuctionResponseDTO dto = new AuctionResponseDTO(auction);
        List<Bid> bids = bidService.findAllBidsByAuction(auction);
        dto.setBids(bids);
        dto.setResult(auctionResultService.getResultByAuction(auction));
        return dto;
    }
}
