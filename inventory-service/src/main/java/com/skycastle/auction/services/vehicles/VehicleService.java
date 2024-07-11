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

package com.skycastle.auction.services.vehicles;

import com.skycastle.auction.entities.forms.requests.VehicleRequestDTO;
import com.skycastle.auction.entities.products.vehicles.Vehicle;
import com.skycastle.auction.entities.products.vehicles.VehicleModel;
import com.skycastle.auction.entities.products.vehicles.VehicleType;
import com.skycastle.auction.utils.Utils;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface VehicleService {

    Vehicle create(VehicleRequestDTO request) throws Exception;
    Vehicle update(Map<String, Object> request, Long id, Long userId) throws IllegalAccessException;
    Page<Vehicle> complexSearch(String searchQuery, int page, int size);
    Page<Vehicle> findAll(int page, int size);
    Page<Vehicle> findVehiclesBySeller(Long sellerId, int page, int size);
    Page<Vehicle> findVehiclesBySellerAndVisibility(Long sellerId,Boolean visibility, int page, int size);
    Page<Vehicle> findVehiclesByVisibility(Boolean visibility,int page, int size );
    Page<Vehicle> filterByOdometerRange(Long min, Long max, int page, int size);
    Page<Vehicle> filterVehiclesByYearRange(int min, int max, int page, int size);
    Page<Vehicle> filterVehiclesByMake(String make, int page, int size);
    Page<Vehicle> filterVehiclesByModel(VehicleModel model, int page, int size);
    Page<Vehicle> filterVehiclesByTransmission(Utils.TRANSMISSION transmission, int page, int size);

    Page<Vehicle> findVehiclesByBuyer(Long buyer, int page, int size);
    void markSold(Vehicle vehicle, Long buyer);
    Vehicle findById(Long id) throws Exception;
    Page<Vehicle> findVehiclesByVehicleTypeAndAndVisibility(VehicleType vehicleType, Boolean visibility, int page, int size);
    int countByVehicleType(VehicleType vehicleType, boolean visibility);
    int countByVehicleModelMake(String make, boolean visibility);
}
