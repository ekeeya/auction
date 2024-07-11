/*
 * Online auctioning system
 *
 * Copyright (c) $today.year- , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.entities.users.dto;

import com.skycastle.auction.utils.Utils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
public class GenericUserDTO implements Serializable {
    private Long id;
    private String username;
    private String email;
    private String address;
    private Map<String, String> settings;
    private String name;
    private Utils.ACCOUNT_TYPE accountType;
    private String nin;
    private String identification;
    private  String firstname;
    private String lastname;
    private Utils.ROLES role ;
    private Utils.Gender gender;
    private String department;
    private Date createdAt;
    private String qrCode;
    private String token;
}
