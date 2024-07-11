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

package com.skycastle.auction.services.watchlist;


import com.skycastle.auction.entities.products.Product;
import com.skycastle.auction.entities.watchlist.WatchList;

import java.util.List;

public interface WatchlistService {

    WatchList create(WatchList watchList);
    void addRemove(Long productId, Long watchListId, boolean add) throws Exception;
    WatchList findByBuyerId(Long buyerId);
    WatchList findById(Long watchListId);
    List<Product> getWatchListProducts(Long watchListId) throws Exception;
}
