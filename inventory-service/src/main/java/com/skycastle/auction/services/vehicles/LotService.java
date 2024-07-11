/*
 * Online auctioning system
 * Copyright (C) 2023 - , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>
 *
 * This program is not free software
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 */

package com.skycastle.auction.services.vehicles;

import com.skycastle.auction.entities.LotEntity;
import com.skycastle.auction.entities.forms.requests.LotRequestDTO;
import com.skycastle.auction.entities.products.Product;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

public interface LotService {

    LotEntity findById(Long lotId);

    Date computeLotStartDate();

    LotEntity createUpdateLot(LotRequestDTO request) throws Exception;

    LotEntity expireLot(LotEntity lot) throws Exception;
    void addProductToLot(LotEntity lot, Product product) throws Exception;

    void removeProductFromLot(LotEntity lot,Product product);

}
