/*
 * Online auctioning system
 *
 * Copyright (c) 2023.  Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {

    public static enum EASY_PAY_RESPONSE_STATUSES{
        Success, Failed, Pending
    }

    public static enum PROVIDER{
        MTN(Values.MTN), AIRTEL(Values.AIRTEL), EASYPAY(Values.EASYPAY);
        private PROVIDER (String val) {
            // force equality between name of enum instance, and value of constant
            if (!this.name().equals(val))
                throw new IllegalArgumentException("Incorrect use of PROVIDER");
        }

        public static class Values {
            public static final String MTN= "MTN";
            public static final String AIRTEL= "AIRTEL";
            public static final String EASYPAY= "EASYPAY";
        }
    }

    public static enum ENV{
        SANDBOX, PRODUCTION
    }
    public static enum TRANSACTION_STATUS{
        SUCCESS, FAILED, PENDING
    }

    public static enum PRODUCT_TYPE{
        COLLECTIONS, DISBURSEMENT
    }
    public static enum SUBSCRIPTION_TYPE{
        SELLER, BUYER
    }

    public static enum SUBSCRIPTION_STATE{
        PENDING, INACTIVE, ACTIVE
    }


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

    public static enum AIRTEL_CODES{
        DP00800001001(Values.DP00800001001), DP00800001003(Values.DP00800001003), DP00800001004(Values.DP00800001004),
        DP00800001005(Values.DP00800001005), DP00800001006(Values.DP00800001006), DP00800001007(Values.DP00800001007),
        DP00800001008(Values.DP00800001008), DP00800001010(Values.DP00800001010), DP00800001024(Values.DP00800001024)
        ;
        private AIRTEL_CODES (String val) {
            // force equality between name of enum instance, and value of constant
            if (!this.name().equals(val))
                throw new IllegalArgumentException("Incorrect use of ELanguage");
        }

        public static class Values {
            public static final String DP00800001001= "Transaction is successful";
            public static final String DP00800001003= "Exceeds withdrawal amount limit(s) / Withdrawal amount limit exceeded";
            public static final String DP00800001004= "Invalid Amount";
            public static final String DP00800001005= "User didn't enter the pin";
            public static final String DP00800001006="In process";
            public static final String DP00800001007="Not enough balance";
            public static final String DP00800001008="Refused";
            public static final String DP00800001010="Transaction not permitted to Payee";
            public static final String DP00800001024="Transaction Timed Out";
        }
    }

    private static Boolean objectHasProperty(Object obj, String propertyName){
        List<Field> properties = getAllFields(obj);
        for(Field field : properties){
            if(field.getName().equalsIgnoreCase(propertyName)){
                return true;
            }
        }
        return false;
    }

    private static List<Field> getAllFields(Object obj){
        List<Field> fields = new ArrayList<Field>();
        getAllFieldsRecursive(fields, obj.getClass());
        return fields;
    }

    private static List<Field> getAllFieldsRecursive(List<Field> fields, Class<?> type) {
        for (Field field: type.getDeclaredFields()) {
            fields.add(field);
        }

        if (type.getSuperclass() != null) {
            fields = getAllFieldsRecursive(fields, type.getSuperclass());
        }

        return fields;
    }
    public static void setProperties(Object object, Map<String, Object> fields){
        BeanWrapper o = new BeanWrapperImpl(object);
        for (Map.Entry<String, Object> property : fields.entrySet()) {
            if(objectHasProperty(object, property.getKey())){
                o.setPropertyValue(property.getKey(), property.getValue());
            }
        }
    }

}
