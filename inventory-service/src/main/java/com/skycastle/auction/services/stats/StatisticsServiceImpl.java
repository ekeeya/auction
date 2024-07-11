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

package com.skycastle.auction.services.stats;

import com.skycastle.auction.dto.stats.ModelStatistics;
import com.skycastle.auction.dto.stats.YearStatistics;
import com.skycastle.auction.entities.Statistics;
import com.skycastle.auction.entities.products.vehicles.VehicleModel;
import com.skycastle.auction.repositories.StatisticsRepository;
import com.skycastle.auction.services.vehicles.VehicleModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StatisticsServiceImpl implements StatisticsService{

    private final StatisticsRepository statisticsRepository;

    private final VehicleModelService vehicleModelService;
    @Override
    public Statistics findStatistics() {
        List<Statistics> stats = statisticsRepository.findAll();
        if (stats.size() > 0){
            return  stats.get(0);
        }
    return null;
    }

    @Override
    public Statistics save(Statistics stat) {
        return statisticsRepository.save(stat);
    }

    @Override
    public void updatePopularity(VehicleModel model, Long count) {
        Map<String, Object> popularity =  new HashMap<>();
        popularity.put("id", model.getId());
        popularity.put("count",count);
        Statistics statistics = findStatistics();
        statistics.setPopularity(popularity);
        statisticsRepository.save(statistics);
    }

    @Override
    public VehicleModel findPopularModel() {
        try{
            Statistics statistics = findStatistics();
            Map<String, Object> popularity = statistics.getPopularity();
            if (popularity != null){
                Long id = (Long) popularity.get("id");
                return id==null ? null : vehicleModelService.findById(id);
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void setMostPopularModel() {
        Statistics statistics = findStatistics();
        List<ModelStatistics> modelStatistics = statistics.getModelStats();
        Long id=0L;
        long count=0L;
        Long max=0L;
        for (ModelStatistics modelStat: modelStatistics) {
            if( modelStat.getCount() > max ){
                max = modelStat.getCount();
                id  = modelStat.getId();
                count=modelStat.getCount();
            }
        }
        VehicleModel model =  vehicleModelService.findById(id);
        updatePopularity(model, count);
    }

    @Override
    public void updateStatistics(VehicleModel vehicleModel, Long transactions, Long count) {
        Statistics statistics = findStatistics();
        // Model and year
        List<ModelStatistics> modelStatistics = statistics.getModelStats() == null ? new ArrayList<>() : statistics.getModelStats();
        List<YearStatistics> yearStatistics =  statistics.getYearStats() ==null ? new ArrayList<>() : statistics.getYearStats();
        List<Integer> years = new ArrayList<>();
        for (YearStatistics yearStats:yearStatistics) {
            years.add(yearStats.getYear());
        }
        for (ModelStatistics modelStat : modelStatistics) {
            Long c = modelStat.getCount() !=null ? modelStat.getCount() : 0L;
            c += count;
            Long t = modelStat.getTransactions() !=null ? modelStat.getTransactions() : 0L;
            t+=transactions;
            if (Objects.equals(modelStat.getId(), vehicleModel.getId())) {
                modelStat.setCount(c);
                modelStat.setTransactions(t);
            } else {
                ModelStatistics model = new ModelStatistics();
                model.setId(vehicleModel.getId());
                model.setName(vehicleModel.getModel());
                model.setCount(1L);
                model.setTransactions(0L);
                modelStatistics.add(model);
                statistics.setModelStats(modelStatistics);
            }
            // set year stats

            if (years.contains(vehicleModel.getYear())){
                //
                int idx =  years.indexOf(vehicleModel.getYear());
                YearStatistics  y = yearStatistics.get(idx);
                y.setCount(y.getCount()+1);
            }else{
                YearStatistics y1 =  new YearStatistics();
                y1.setYear(vehicleModel.getYear());
                y1.setCount(1L);
                yearStatistics.add(y1);
                statistics.setYearStats(yearStatistics);
            }
        }
        statisticsRepository.save(statistics);
    }


}
