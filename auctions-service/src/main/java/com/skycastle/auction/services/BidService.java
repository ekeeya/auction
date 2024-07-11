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

package com.skycastle.auction.services;

import com.skycastle.auction.dtos.bids.BidRequestDTO;
import com.skycastle.auction.entities.Auction;
import com.skycastle.auction.entities.Bid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BidService {
    Page<Bid> filterBidsByAuction(Auction auction, int page, Integer size);
    List<Bid> findAllBidsByAuction(Auction auction);
    Page<Bid> filterBidsByBuyer(Long buyerId, int page, int size);

    Bid saveBid(BidRequestDTO request, Auction auction);
}
