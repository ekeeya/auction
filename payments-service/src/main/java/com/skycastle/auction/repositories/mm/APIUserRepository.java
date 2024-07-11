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

package com.skycastle.auction.repositories.mm;

import com.skycastle.auction.entities.mm.APIUser;
import com.skycastle.auction.entities.mm.MobileMoneyProduct;
import com.skycastle.auction.utils.Utils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface APIUserRepository extends JpaRepository<APIUser, Long> {
    APIUser findAPIUserByProductAndProvider(MobileMoneyProduct product, Utils.PROVIDER provider);
    APIUser findAPIUserByProduct_ProductTypeAndProvider(Utils.PRODUCT_TYPE type, Utils.PROVIDER provider);
}
