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
import com.skycastle.auction.repositories.AuctionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionResultServiceImpl implements AuctionResultService{

    private final AuctionResultRepository auctionResultRepository;
    @Override
    public AuctionResult getResultByAuction(Auction auction) {
        return auctionResultRepository.findAuctionResultByAuction(auction);
    }

    @Override
    public Page<AuctionResult> filterResultsByBidder(Long bidderId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        //TODO will finish this later using a custom query
        return null;
    }

    @Override
    public Page<AuctionResult> filterResultsByWinner(Long winner, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return auctionResultRepository.findAuctionResultsByWinner(winner, pageable);
    }
}
