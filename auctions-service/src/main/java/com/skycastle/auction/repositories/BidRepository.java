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
import com.skycastle.auction.entities.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    Page<Bid> findBidsByAuction(Auction auction, Pageable pageable);
    List<Bid> findBidsByAuction(Auction auction);
    Page<Bid> findBidsByBuyer(Long buyerId, Pageable pageable);
}
