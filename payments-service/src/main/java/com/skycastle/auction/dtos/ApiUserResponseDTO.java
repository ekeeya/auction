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


import com.skycastle.auction.entities.mm.APIUser;
import com.skycastle.auction.utils.Utils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class ApiUserResponseDTO implements Serializable {
    private Long id;
    private Utils.PROVIDER provider;
    private Utils.ENV environment;

    @Getter
    @Setter
    public  class Product{
        private String name;
        private Utils.PRODUCT_TYPE productType;
        private String callBackUrl;
    }
    private Product product;

    public  ApiUserResponseDTO(APIUser u){
        this.setEnvironment(u.getEnvironment());
        this.setId(u.getId());
        this.setProvider(u.getProvider());
        Product p =  new Product();
        p.setName(u.getProduct().getName());
        p.setProductType(u.getProduct().getProductType());
        p.setCallBackUrl(u.getProduct().getCallBackUrl());
        this.setProduct(p);
    }
}
