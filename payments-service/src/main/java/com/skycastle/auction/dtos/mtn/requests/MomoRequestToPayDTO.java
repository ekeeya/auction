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

package com.skycastle.auction.dtos.mtn.requests;

import com.skycastle.auction.dtos.BaseRequestToPay;
import com.skycastle.auction.dtos.mtn.Payer;
import lombok.Data;

@Data
public class MomoRequestToPayDTO extends BaseRequestToPay {
    private String externalId;
    private Payer payer;
    private String currency;
    private String payerMessage;
    private String payeeNote;
}
