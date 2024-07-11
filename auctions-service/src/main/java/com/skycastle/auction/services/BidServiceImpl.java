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
import com.skycastle.auction.repositories.BidRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BidServiceImpl implements BidService{

    private final BidRepository bidRepository;
    private final EntityManager entityManager;
    @Override
    public Page<Bid> filterBidsByAuction(Auction auction, int page, Integer size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return bidRepository.findBidsByAuction(auction, pageable);
    }

    @Override
    public List<Bid> findAllBidsByAuction(Auction auction) {
        return bidRepository.findBidsByAuction(auction);
    }

    @Override
    public Page<Bid> filterBidsByBuyer(Long buyerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return bidRepository.findBidsByBuyer(buyerId, pageable);
    }

    @Override
    public Bid saveBid(BidRequestDTO request, Auction auction) {
        Bid bid = new Bid();
        bid.setAmount(request.getAmount());
        bid.setAuction(auction);
        bid.setBuyer(request.getBuyer());

        log.info("Saved new bid: {}", bid);
        //entityManager.refresh(newBid);
        return bidRepository.save(bid);
    }
}
