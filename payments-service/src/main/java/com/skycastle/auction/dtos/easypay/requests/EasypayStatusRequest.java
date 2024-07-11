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

package com.skycastle.auction.dtos.easypay.requests;

import com.skycastle.auction.dtos.easypay.constants;
import lombok.Data;

import java.io.Serializable;

@Data
public class EasypayStatusRequest implements Serializable {
    private String username;
    private String password;
    private String action= constants.ACTIONS.MM_STATUS.toString();
    private String reference;
}
