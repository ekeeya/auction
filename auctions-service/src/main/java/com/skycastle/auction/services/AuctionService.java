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
import com.skycastle.auction.utils.Utils;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface AuctionService {

    Page<Auction> fetchAll(int page, int size);
    Auction findById(Long id);
    Auction findAuctionByUuid(String uuid);
    Auction setAuctionSession(Auction auction) throws Exception;
    Auction completeAuction(Auction auction);
    Auction findAuctionByProductId(Long productId);
    Page<Auction> filterByStatus(Utils.AUCTION_STATUS status, int page, int size);
    Page<Auction> filterAuctionsByDateRange(Date startDate, Date endDate, int page, int size);

    Page<Auction> filterAuctionsStatusAndByDateRange(Utils.AUCTION_STATUS status, Date startDate, Date endDate, int page, int size);
    List<Auction> filterAuctionsOlderOrEqualToStartDateAndStatus(Date date, Utils.AUCTION_STATUS status); // will be used by the daemon task to start an auction
    List<Auction> filterAuctionsOlderOrEqualToEndDateAndStatus(Date date, Utils.AUCTION_STATUS status); // will be used by the daemon to end an auction.

    Page<Auction> findAuctionsBySellerAndStatus(Long seller, Utils.AUCTION_STATUS status, int page, int size);
    void startAuctions();
    void endAuctions();
}
