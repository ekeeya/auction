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

package com.skycastle.auction.services.vehicles;

import com.skycastle.auction.clients.AuctionClientStore;
import com.skycastle.auction.components.ContextProvider;
import com.skycastle.auction.dto.AuctionPatchDTO;
import com.skycastle.auction.entities.LotEntity;
import com.skycastle.auction.entities.forms.requests.LotRequestDTO;
import com.skycastle.auction.entities.products.Product;
import com.skycastle.auction.entities.products.vehicles.Vehicle;
import com.skycastle.auction.repositories.LotRepository;
import com.skycastle.auction.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LotServiceImpl implements LotService{

    private final LotRepository lotRepository;
    private final VehicleService vehicleService;

    private final AuctionClientStore auctionClientStore;

    private final ContextProvider contextProvider;

    @Value("${inventory.lot.duration}")
    private int lotDefaultDuration; // in hours


    protected Date latestAuctionEndDate(LotEntity lot){
        List<Product> products =  lot.getProducts()==null ? new ArrayList<>() : lot.getProducts();
        List<Date> dates = new ArrayList<>();
        Date maxDate =  new Date(0); // epoch
        products.forEach(product->{
            Map<String, Object> auction = (Map<String, Object>) auctionClientStore.getProductAuction(contextProvider.getAuthHeaderValue(),product.getId());
            if(auction != null){
                Map<String, Object> data = (Map<String, Object>) auction.get("data");
                String sDate = (String) data.get("endDate");
                SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    if(sDate !=null){
                        dates.add(Utils.timeZonedDate(dateFormat.parse(sDate)));
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        if(dates.size() > 0 ){
            for (Date date:dates ) {
                if(date.compareTo(maxDate) > 0){
                    maxDate = date;
                }
            }
            return maxDate;
        }
        return null;
    }
    @Override
    public LotEntity findById(Long lotId) {
        try{
            return lotRepository.findLotEntityById(lotId);
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Date computeLotStartDate() {
        LotEntity recentPendingLot =  lotRepository.findDistinctFirstByStatusOrderByStartDateAsc(Utils.LOT_STATUSES.PENDING);
        if(recentPendingLot != null){
            Date endDate =  recentPendingLot.getEndDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.SECOND, 5); // add 5 second to the end of the latest lot
            return calendar.getTime();
        }
        return Calendar.getInstance().getTime();
    }

    @Override
    public LotEntity createUpdateLot(LotRequestDTO request) throws Exception {
        LotEntity lotEntity;
        if(request.getId() != null){
            lotEntity =  findById(request.getId());
        }else{
            lotEntity =  new LotEntity();
        }
        lotEntity.setSeller(request.getSeller());
        Date startDate = computeLotStartDate();
        lotEntity.setStartDate(startDate);
        Calendar calendar =  Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.HOUR, lotDefaultDuration);
        // by default set it to config value from inventory-service.properties we will later adjust it according to how
        // vehicle Auction durations in this lot
        lotEntity.setEndDate(calendar.getTime());
        List<Long> vehicleIds =  request.getVehicles();
        LotEntity newLot = lotRepository.save(lotEntity);
        if (vehicleIds!= null && vehicleIds.size() > 0){
            for (Long vehicleId: vehicleIds
            ) {
                Vehicle vehicle =  vehicleService.findById(vehicleId);
                addProductToLot(newLot, vehicle);
            }
        }
        return newLot;
    }

    @Override
    public LotEntity expireLot(LotEntity lot) throws Exception {
        lot.setStatus(Utils.LOT_STATUSES.EXPIRED);
        lotRepository.save(lot);
        // Create another one immediately if this lot had vehicles in
        List<Product> products =  lot.getProducts();
        if(products !=null && products.size() > 0 ){
            Vehicle vehicle = (Vehicle) products.get(0);
            LotRequestDTO lotRequest =  new LotRequestDTO();
            lotRequest.setSeller(vehicle.getSeller());
            LotEntity newLot =  createUpdateLot(lotRequest);
            for (Product product: products ) {
                //remove vehicle from expired lot
                removeProductFromLot(lot, product);
                // add them to new lot
                addProductToLot(newLot, product);
            }
        }
        return null;
    }

    @Override
    public void addProductToLot(LotEntity lot, Product product) throws Exception {
        try{
            if(product.getInspectionReport() == null){
                throw new Exception("Vehicle you are trying to add has not been inspected");
            }
            if (lot != null){
                List<Product> products =  lot.getProducts()==null ? new ArrayList<>() : lot.getProducts();
                Date maxDate = latestAuctionEndDate(lot);
                Calendar c =  Calendar.getInstance();
                if(maxDate !=null){
                    c.setTime(maxDate);
                    c.add(Calendar.SECOND,2);//just add 2 second it doesn't matter, does it?
                }
                AuctionPatchDTO patchRequest = new AuctionPatchDTO();
                patchRequest.setStartDate(c.getTime());
                Map<String, Object> r = (Map<String, Object>) auctionClientStore.updateAuction(contextProvider.getAuthHeaderValue(),product.getAuctionId(),patchRequest);
                String returnStartDate = (String) r.get("startDate");
                String returnEndDate = (String) r.get("endDate");
                Date startDate = null;
                Date endDate = null;
                SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if(returnEndDate !=null){
                    endDate = Utils.timeZonedDate(dateFormat.parse(returnEndDate));
                }
                if(returnStartDate !=null){
                    startDate = Utils.timeZonedDate(dateFormat.parse(returnStartDate));
                }
                if (products.size() == 0){
                    lot.setStartDate(startDate);
                }
                products.add(product);
                lot.setProducts(products);
                lot.setEndDate(endDate);
                product.setLot(lotRepository.save(lot));
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeProductFromLot(LotEntity lot, Product product) {
        if (lot != null){
            lot.getProducts().remove(product);
            lotRepository.save(lot);
        }
    }
}
