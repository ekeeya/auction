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

import com.skycastle.auction.entities.Auction;
import com.skycastle.auction.entities.AuctionResult;
import org.springframework.data.domain.Page;

public interface AuctionResultService {
    AuctionResult getResultByAuction(Auction auction);
    Page<AuctionResult> filterResultsByBidder(Long bidderId, int page, int size); // a bidder is a buyer
    Page<AuctionResult> filterResultsByWinner(Long winner, int page, int size);
}
