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

package com.skycastle.auction.dtos.airtel.responses;

import com.skycastle.auction.utils.Utils;
import lombok.Data;

import java.io.Serializable;

@Data
public class PaymentStatus implements Serializable {
    private String code;
    private String message;
    private String result_code;
    private Utils.AIRTEL_CODES response_code;
    private Boolean success;
}
