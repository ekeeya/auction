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
public class ApiUserConfigRequestDTO {
    private Utils.PROVIDER provider;
    private Utils.ENV environment;
    private  Long  productId;
    private String primaryKey;
    private String secondaryKey;
    private String clientId;
    private String clientSecretKey;
}
