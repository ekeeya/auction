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

package com.skycastle.auction.services.stats;

import com.skycastle.auction.entities.Statistics;
import com.skycastle.auction.entities.products.vehicles.VehicleModel;

import java.util.Map;

public interface StatisticsService {

    Statistics findStatistics();
    Statistics save(Statistics stat);
    VehicleModel findPopularModel();
    void updatePopularity(VehicleModel model, Long count);

    void setMostPopularModel();

    void updateStatistics(VehicleModel model, Long transactions, Long count);

}
