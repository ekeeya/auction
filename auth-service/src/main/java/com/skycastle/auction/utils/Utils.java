/*
 * Online auctioning system
 * Copyright (C) 2022 - , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *

 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>
 *
 * This program is not free software
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.utils;

public class Utils {



    public static enum ROLES {
        ROLE_ADMIN,  ROLE_SELLER, ROLE_PRE_VERIFIED, ROLE_BUYER, ROLE_ANONYMOUS
    }
    public static  enum Gender{
        MALE, FEMALE
    }

    public static enum ACCOUNT_STATUS {
        ACTIVE, DISABLED
    }

    public static enum ACCOUNT_TYPE{
        ADMIN(Values.ADMIN), BUYER(Values.BUYER), SELLER(Values.SELLER),ANONYMOUS(Values.ANONYMOUS);
        private ACCOUNT_TYPE (String val) {
            // force equality between name of enum instance, and value of constant
            if (!this.name().equals(val))
                throw new IllegalArgumentException("Incorrect use of ELanguage");
        }

        public static class Values {
            public static final String ADMIN= "ADMIN";
            public static final String BUYER= "BUYER";
            public static final String SELLER= "SELLER";
            public static final String ANONYMOUS= "ANONYMOUS";
        }
    }

}
