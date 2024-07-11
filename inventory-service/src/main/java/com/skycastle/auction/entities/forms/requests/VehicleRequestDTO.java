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

package com.skycastle.auction.entities.forms.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skycastle.auction.utils.Utils;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class VehicleRequestDTO  implements Serializable {

    private String name;
    private String description;
    private Map<String, String> properties;
    private Long seller;
    private Utils.PRODUCT_CONDITION condition =  Utils.PRODUCT_CONDITION.USED;
    private List<String> images=new ArrayList<>();
    private Boolean isInspected = false;
    private String vin;
    private Long odometer=0l;
    private Long model;
    private Long vehicleType;
    private Boolean buyNow;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal sellingPrice=new BigDecimal(0);
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal reservePrice=new BigDecimal(0);
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal minBidAmount=new BigDecimal(100000);

    private Long bodyStyle;
    private String regNo;
    private String transmission;
    private String fuelType;
}
