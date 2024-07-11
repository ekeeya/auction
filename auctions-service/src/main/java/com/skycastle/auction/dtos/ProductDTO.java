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

package com.skycastle.auction.dtos;

import com.skycastle.auction.utils.Utils;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ProductDTO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Map<String, String> properties = new HashMap<>();
    private Long seller;
    private String condition;
    private List<String> images;
    private boolean isInspected = false;
    private String status;
    private String categoryName;
    private String vin;
    private Long odometer;
    private String model;
    private String make;
    private String vehicleType;
    private Boolean buyNow;
    private BigDecimal sellingPrice=new BigDecimal(0);
    private BigDecimal reservePrice=new BigDecimal(0);
    private String bodyStyle;
    private String regNo;
    private String transmission;
    private String fuelType;
}
