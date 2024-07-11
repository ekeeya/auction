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
import com.skycastle.auction.repositories.AuctionRepository;
import com.skycastle.auction.utils.Utils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuctionServiceImpl implements AuctionService{

    private final AuctionRepository auctionRepository;

    @Override
    public Page<Auction> fetchAll(int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return auctionRepository.findAll(pageable);
    }

    @Override
    public Auction findById(Long id) {
        return auctionRepository.findById(id).get(); // will raise an exception if nothing is found
    }

    @Override
    public Auction findAuctionByUuid(String uuid) {
        return auctionRepository.findAuctionByUuid(uuid);
    }

    @Override
    public Auction setAuctionSession(Auction auction) throws Exception {
        Auction a =  findAuctionByProductId(auction.getProduct());
        if (a!=null){
            String msg = "A product can only have one auction session configured";
            log.error(msg);
            throw new Exception(msg);
        }
        return auctionRepository.save(auction);
    }

    @Override
    public Auction completeAuction(Auction auction) {
        auction.setStatus(Utils.AUCTION_STATUS.COMPLETED);
        return auctionRepository.save(auction);
    }

    @Override
    public Auction findAuctionByProductId(Long productId) {
        return auctionRepository.findAuctionByProduct(productId);
    }

    @Override
    public Page<Auction> filterByStatus(Utils.AUCTION_STATUS status, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return auctionRepository.findAuctionsByStatus(status, pageable);
    }

    @Override
    public Page<Auction> filterAuctionsByDateRange(Date startDate, Date endDate, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return auctionRepository.findAuctionByStartDateIsBetween(startDate, endDate, pageable);
    }

    @Override
    public Page<Auction> filterAuctionsStatusAndByDateRange(Utils.AUCTION_STATUS status, Date startDate, Date endDate, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return auctionRepository.findAuctionsByStatusAndStartDateIsBetween(status, startDate, endDate, pageable);
    }

    @Override
    public List<Auction> filterAuctionsOlderOrEqualToStartDateAndStatus(Date date, Utils.AUCTION_STATUS status) {
        if (status == null){
            status = Utils.AUCTION_STATUS.PENDING;
        }
        return auctionRepository.findAuctionByStartDateIsGreaterThanEqualAndStatusIs(date, status);
    }

    @Override
    public List<Auction> filterAuctionsOlderOrEqualToEndDateAndStatus(Date date, Utils.AUCTION_STATUS status) {
        if (status == null){
            status = Utils.AUCTION_STATUS.COMPLETED;
        }
        return auctionRepository.findAuctionByEndDateIsGreaterThanEqualAndStatusIs(date, status);
    }

    @Override
    public Page<Auction> findAuctionsBySellerAndStatus(Long seller, Utils.AUCTION_STATUS status, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return auctionRepository.findAuctionsBySellerAndStatus(seller,status,pageable);
    }

    @Override
    public void startAuctions() {
        log.info("Looking for auctions to start");
        Date now =  new Date();
        log.info("It is :"+ now);
        List<Auction> auctions = filterAuctionsOlderOrEqualToStartDateAndStatus(now, Utils.AUCTION_STATUS.PENDING);
        for (Auction auction: auctions ) {
            auction.setStatus(Utils.AUCTION_STATUS.IN_PROGRESS);
        }
    }

    @Override
    public void endAuctions() {
        log.info("Looking for due auctions to End");
        Date now =  new Date();
        log.info("It is :"+ now);
        List<Auction> auctions = filterAuctionsOlderOrEqualToEndDateAndStatus(now, Utils.AUCTION_STATUS.IN_PROGRESS);
        for (Auction auction: auctions ) {
            auction.setStatus(Utils.AUCTION_STATUS.COMPLETED);
        }
    }
}
