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

import com.skycastle.auction.dto.stats.MakeStatistics;
import com.skycastle.auction.dto.stats.ModelStatistics;
import com.skycastle.auction.dto.stats.YearStatistics;
import com.skycastle.auction.entities.BaseEntity;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import com.vladmihalcea.hibernate.type.json.JsonBlobType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Transactional
@Data
public class Statistics extends BaseEntity {
    @Type(PostgreSQLHStoreType.class)
    @Column(columnDefinition = "hstore")
    private Map<String, Object> popularity;
    @Type(JsonBlobType.class)
    @Column(name="model_stats", columnDefinition = "jsonb")
    private List<ModelStatistics> modelStats;

    @Type(JsonBlobType.class)
    @Column(name="make_stats", columnDefinition = "jsonb")
    private List<MakeStatistics> makeStats;

    @Type(JsonBlobType.class)
    @Column(name="year_stats", columnDefinition = "jsonb")
    private List<YearStatistics> yearStats;


}
