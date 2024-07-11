/*
 * Online auctioning system
 *
 * Copyright (c) 2022.  Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.entities.forms.responses;

import com.skycastle.auction.entities.products.Category;
import lombok.Data;

import java.io.Serializable;


@Data
public class CategoryResponseDTO implements Serializable {
    private Long id;
    private String categoryName;

    public CategoryResponseDTO(Category category){
        this.setId(category.getId());
        this.setCategoryName(category.getCategoryName());
    }
}
