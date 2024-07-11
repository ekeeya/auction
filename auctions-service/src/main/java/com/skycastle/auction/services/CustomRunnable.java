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

package com.skycastle.auction.services;

import java.util.function.Function;

public class CustomRunnable implements IdentifiableRunnable{

    private Long id;
    private Function<Long, Long> callBack;

    public CustomRunnable(Function<Long, Long> callBack){
        this.callBack=callBack;
    }
    @Override
    public Long getId() {
        return id;
    }
    public  void  setId(Long id){
        this.id=id;
    }

    @Override
    public void run() {
        Long result = callBack.apply(id);
        setId(result);
    }
}
