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

package com.skycastle.auction.repositories;

import com.skycastle.auction.entities.Auction;
import com.skycastle.auction.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Auction findAuctionByProduct(Long productId);
    Auction findAuctionByUuid (String uuid);
    Page<Auction> findAll(Pageable pageable);
    Page<Auction> findAuctionsByStatus(Utils.AUCTION_STATUS status, Pageable pageable);
    Page<Auction> findAuctionByStartDateIsBetween(Date startDate, Date endDate, Pageable pageable);

    Page<Auction> findAuctionsByStatusAndStartDateIsBetween(Utils.AUCTION_STATUS status,Date startDate, Date endDate, Pageable pageable);
    List<Auction> findAuctionByStartDateIsGreaterThanEqualAndStatusIs(Date date, Utils.AUCTION_STATUS status);
    List<Auction> findAuctionByEndDateIsGreaterThanEqualAndStatusIs(Date date, Utils.AUCTION_STATUS status);

    Page<Auction> findAuctionsBySellerAndStatus(Long seller, Utils.AUCTION_STATUS status, Pageable pageable);
}
