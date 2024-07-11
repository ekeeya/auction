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

package com.skycastle.auction.dtos.mtn.requests;

import com.skycastle.auction.utils.Utils;
import lombok.Data;

@Data
public class MomoAccessTokenRequestDTO {
    private String apiKey;
    private String primaryKey;
    private String userUuid;
    private Utils.PROVIDER provider;
}
