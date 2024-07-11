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

package com.skycastle.auction.repositories;

import com.skycastle.auction.entities.LotEntity;
import com.skycastle.auction.utils.Utils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<LotEntity, Long> {

    LotEntity findLotEntityById(Long id);
    LotEntity findLotEntityBySellerAndStatus(Long seller, Utils.LOT_STATUSES status);
    List<LotEntity> findLotEntitiesByStatus(Utils.LOT_STATUSES status);


    LotEntity findDistinctFirstByStatusOrderByStartDateAsc(Utils.LOT_STATUSES status);
}
