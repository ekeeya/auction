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

package com.skycastle.auction.dto;


import com.skycastle.auction.utils.Utils;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class SellerDTO implements Serializable {
    private Utils.ACCOUNT_TYPE accountType;
    private String username;
    private String password;
    private Utils.ROLES role = Utils.ROLES.ROLE_PRE_VERIFIED;
    private String email;
    private String address;
    private Map<String, String> settings = new HashMap<>();
    private Boolean isExpired = false;
    private Utils.ACCOUNT_STATUS status = Utils.ACCOUNT_STATUS.ACTIVE;
    private String name;
    private String nin;
    private String identification;
    private Boolean isApproved =  false;
}
