/*
 * Online auctioning system
 *
 * Copyright (c) 2022.  Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.services.seller;

import com.skycastle.auction.entities.users.Seller;
import com.skycastle.auction.repositories.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService{

    private final SellerRepository sellerRepository;
    @Override
    public Seller findById(Long id) {
        return sellerRepository.findById(id).get();
    }

    @Override
    public Seller approveSeller(Long sellerId, boolean approved) {
        Seller seller =  findById(sellerId);
        seller.setIsApproved(approved);
        return sellerRepository.save(seller);
    }
}
