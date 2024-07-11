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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skycastle.auction.entities.products.Product;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class LotRequestDTO implements Serializable {

    private Long id; // for updating

    @NotNull
    private Long seller;
    private List<Long> vehicles;
}
