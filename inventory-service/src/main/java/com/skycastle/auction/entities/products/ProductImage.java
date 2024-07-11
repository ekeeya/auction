/*
 * Online auctioning system
 *
 * Copyright (c)  $today.year- , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.entities.products;

import com.skycastle.auction.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="product_image")
@Data
public class ProductImage extends BaseEntity {
    private String name;

    @Column(name="content", columnDefinition = "TEXT")
    private String content;

    @Column(name="file_type")
    private String fileType;

}
