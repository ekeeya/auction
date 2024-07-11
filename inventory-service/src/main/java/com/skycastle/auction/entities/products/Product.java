/*
 * Online auctioning system
 *
 * Copyright (c)  2022 - , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
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
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.skycastle.auction.entities.BaseEntity;
import com.skycastle.auction.entities.LotEntity;
import com.skycastle.auction.utils.Utils;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorColumn(name = "category")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DynamicInsert
@DynamicUpdate
@Transactional
@Data
public class Product extends BaseEntity {

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Type(PostgreSQLHStoreType.class)
    @Column(columnDefinition = "hstore")
    private Map<String, String> properties = new HashMap<>();

    private Long seller;

    private Long buyer;

    @Enumerated(EnumType.STRING)
    private Utils.PRODUCT_CONDITION condition;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @OneToMany
    private List<ProductImage> images;

    @Column(name="is_inspected")
    private boolean isInspected = false;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="inspection_report_id", referencedColumnName = "id")
    private InspectionReport inspectionReport;

   @Transient
    private String categoryName;

    @Enumerated(EnumType.STRING)
    private Utils.STATUS status = Utils.STATUS.IN_EVALUATION;

    @Column(name = "buy_now")
    private Boolean buyNow =  false;

    @Column(name="selling_price",columnDefinition = "NUMERIC(38,2) DEFAULT 0.00" )
    private BigDecimal sellingPrice =  new BigDecimal(0); // set if buyNow is true

    @Column(name="reserve_price", columnDefinition = "NUMERIC(38,2) DEFAULT 0.00")
    private BigDecimal reservePrice =  new BigDecimal(0);

    @Column(name="min_bid_amount", columnDefinition = "NUMERIC(38,2) DEFAULT 0.00")
    private BigDecimal minBidAmount =  new BigDecimal(0);

    @Column(name="auction_id") // we shall set it once we add an auction we need it to speed up auction queries across micro services
    private Long auctionId;

    @Column(name="is_visible", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVisible=false;

    @ManyToOne
    @JoinColumn(name="lot_id")
    private LotEntity lot;

    @PostLoad
    public void setCategory(){
        this.setCategoryName(this.getCategory().getCategoryName());
    }

    @JsonIgnore
    public InspectionReport getInspectionReport() {
        return inspectionReport;
    }

    @JsonIgnore
    public LotEntity getLot() {
        return lot;
    }
}
