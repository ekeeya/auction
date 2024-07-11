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

package com.skycastle.auction.entities.forms.requests;

import com.skycastle.auction.dtos.mtn.Payer;
import com.skycastle.auction.utils.Utils;
import lombok.Data;

import java.io.Serializable;

@Data
public class SubscriptionPaymentRequest implements Serializable {
    private String msisdn;
    private Double amount;
    private Boolean isTelecom=true;
    private Utils.ENV env;
}
