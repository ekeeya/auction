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

package com.skycastle.auction.entities.products;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.skycastle.auction.entities.BaseEntity;
import com.skycastle.auction.entities.products.vehicles.Vehicle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="inspection_report")
@Getter
@Setter
public class InspectionReport extends BaseEntity {
    private String name;

    @OneToOne(mappedBy = "inspectionReport")
    private Product product;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Lob
    byte[] content;

    @JsonIgnore
    public Product getProduct() {
        return product;
    }
}
