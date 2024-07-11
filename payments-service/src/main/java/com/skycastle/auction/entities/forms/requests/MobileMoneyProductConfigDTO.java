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

package com.skycastle.auction.entities.forms.requests;

import com.skycastle.auction.utils.Utils;
import lombok.Data;


@Data
public class MobileMoneyProductConfigDTO{

    private String name;
    private Utils.PRODUCT_TYPE productType;
    private Utils.PROVIDER provider;
    private String callBackUrl;
    private String description;
    private String hashKey;
    private String primaryKey;
    private String secondaryKey;
    private String clientId;
    private String username;
    private String password;
    private String clientSecretKey;
    private Utils.ENV environment;
}
