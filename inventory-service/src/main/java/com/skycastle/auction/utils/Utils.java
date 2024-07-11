/*
 * Online auctioning system
 *
 * Copyright (c)  2022 - , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
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
    public static enum FUEL_TYPES {
        ELECTRIC,  DIESEL, FLEXIBLE_FUEL, PETROLEUM, HYBRID
    }

    public static enum TRANSMISSION {
        AUTOMATIC,  MANUAL
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

    public static enum  PRODUCT_CATEGORIES{
        VEHICLES(Values.VEHICLES), FURNITURE(Values.FURNITURE);

        PRODUCT_CATEGORIES(String value) {
            if (!this.name().equals(value))
                throw new IllegalArgumentException("Incorrect use of ELanguage");
        }

        public static class Values {
            public static final String VEHICLES= "Vehicles";
            public static final String FURNITURE= "Furniture";
        }
    }

    public static enum LOT_STATUSES{
        ACTIVE, PENDING, COMPLETED, CANCELLED, EXPIRED
    }

    public static enum PRODUCT_CONDITION{
        USED, NEW, SALVAGED, REFURBISHED
    }

    public static enum STATUS{
        AVAILABLE, SOLD, IN_EVALUATION, REJECTED
    }

    //https://stackoverflow.com/questions/10156626/how-to-check-if-a-given-class-has-a-field-and-it-was-initialized
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

    public static boolean exists(Map map, String key){
        Object v = map.getOrDefault(key, null);
        return v != null;
    }

    public static Object setProperties(Object object, Map<String, Object> fields){
        BeanWrapper o = new BeanWrapperImpl(object);
        for (Map.Entry<String, Object> property : fields.entrySet()) {
            if(objectHasProperty(object, property.getKey())){
                o.setPropertyValue(property.getKey(), property.getValue());
            }
        }
        return object;
    }

    public static Date timeZonedDate(Date date){
        TimeZone systemTimeZone = TimeZone.getDefault();
        int systemOffsetMillis = systemTimeZone.getOffset(date.getTime());
        return new Date(date.getTime() + systemOffsetMillis);
    }


}
