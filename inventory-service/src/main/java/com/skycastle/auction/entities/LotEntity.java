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

package com.skycastle.auction.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skycastle.auction.entities.products.Product;
import com.skycastle.auction.entities.products.ProductImage;
import com.skycastle.auction.utils.Utils;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Transactional
@Data
@Table(name="lot")
public class LotEntity extends BaseEntity{

    private Long seller;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @OneToMany(mappedBy = "lot")
    private List<Product> products;

    @Enumerated(EnumType.STRING)
    private Utils.LOT_STATUSES status = Utils.LOT_STATUSES.PENDING;
}
