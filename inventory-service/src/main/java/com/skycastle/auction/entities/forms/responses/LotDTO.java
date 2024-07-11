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

package com.skycastle.auction.entities.forms.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skycastle.auction.entities.LotEntity;
import com.skycastle.auction.entities.products.vehicles.Vehicle;
import com.skycastle.auction.utils.Utils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LotDTO implements Serializable {

    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    private String status;

    public LotDTO(LotEntity lot){
        setId(lot.getId());
        setStatus(lot.getStatus().toString());
        setStartDate(lot.getStartDate());
        setEndDate(lot.getEndDate());
    }
}
