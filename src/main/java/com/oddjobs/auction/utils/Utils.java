package com.oddjobs.auction.utils;

public class Utils {



    public static enum ROLES {
        ROLE_ADMIN,  ROLE_USER, ROLE_SELLER, ROLE_PRE_VERIFIED, ROLE_BUYER
    }
    public static  enum Gender{
        MALE, FEMALE
    }

    public static enum ACCOUNT_STATUS {
        ACTIVE, DISABLED
    }

    public static enum ACCOUNT_TYPE{
        ADMIN(Values.ADMIN), BUYER(Values.BUYER), SELLER(Values.SELLER);
        private ACCOUNT_TYPE (String val) {
            // force equality between name of enum instance, and value of constant
            if (!this.name().equals(val))
                throw new IllegalArgumentException("Incorrect use of ELanguage");
        }

        public static class Values {
            public static final String ADMIN= "ADMIN";
            public static final String BUYER= "BUYER";
            public static final String SELLER= "SELLER";
        }
    }





}
