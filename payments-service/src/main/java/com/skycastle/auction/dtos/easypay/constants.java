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

package com.skycastle.auction.dtos.easypay;

public class constants {


    public static enum ACTIONS{
        MM_DEPOSIT(Values.MM_DEPOSIT), MM_PAYOUT(Values.MM_PAYOUT) , MM_STATUS(Values.MM_STATUS);
        private ACTIONS (String val) {
            // force equality between name of enum instance, and value of constant
            if (!this.name().equals(val))
                throw new IllegalArgumentException("Incorrect use of ACTIONS");
        }

        public static class Values {
            public static final String MM_DEPOSIT= "mmdeposit";
            public static final String MM_PAYOUT= "mmpayout";
            public static final String MM_STATUS= "mmstatus";
        }
    }
}
