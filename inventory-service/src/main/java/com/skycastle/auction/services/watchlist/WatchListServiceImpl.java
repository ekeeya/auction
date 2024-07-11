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
import com.skycastle.auction.repositories.ProductsRepository;
import com.skycastle.auction.repositories.watchlist.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatchListServiceImpl implements WatchlistService{

    private final WatchlistRepository watchlistRepository;
    private final ProductsRepository  productsRepository;
    @Override
    public WatchList create(WatchList watchList) {
        return watchlistRepository.save(watchList);
    }

    @Override
    public void addRemove(Long productId, Long watchListId, boolean add)throws  Exception {

        if (productsRepository.findById(productId).isEmpty()){
            throw new Exception("Product list with id %d does not exist".formatted(productId));
        }
        Product product =  productsRepository.findById(productId).get();
        WatchList watchList = findById(watchListId);
        if (watchList == null){
            throw new Exception("Watch list with id %d does not exist".formatted(watchListId));
        }
        List<Product> products =  watchList.getProducts();
        if(add){
            products.add(product);
        }else {
            products.remove(product);
        }
        watchList.setProducts(products);
        watchlistRepository.save(watchList);
    }

    @Override
    public WatchList findByBuyerId(Long buyerId) {
        try{
            return watchlistRepository.findWatchListByBuyerId(buyerId);
        }catch (Exception e){
            log.error("Watchlist with buyer Id %d does not exist".formatted(buyerId));
            return null;
        }
    }

    @Override
    public WatchList findById(Long watchListId) {
        try{
            return watchlistRepository.findById(watchListId).get();
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<Product> getWatchListProducts(Long watchListId) throws Exception {
        WatchList watchList = findById(watchListId);
        if(watchList ==null){
            throw new Exception("Watch list with Id %d does not exist".formatted(watchListId));
        }
        return watchList.getProducts();
    }
}
