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

package com.skycastle.auction.dtos.mtn.responses;


import com.skycastle.auction.dtos.mtn.requests.MomoRequestToPayDTO;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


@Data
public class RequestToPayStatusResponseDTO implements Serializable {


    public static enum MOMO_TRANS_STATUS{
        SUCCESSFUL, PENDING, FAILED, EXPIRED
    }
    private String financialTransactionId;
    private MOMO_TRANS_STATUS status;
    private String externalId;
    private Object payer;
    private Object reason;
    private String currency;

    private String payerMessage;
    private String payeeNote;
    private Double amount;



}
