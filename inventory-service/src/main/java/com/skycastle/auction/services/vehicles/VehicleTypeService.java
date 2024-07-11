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

import com.skycastle.auction.entities.products.vehicles.VehicleType;

import java.util.List;

public interface VehicleTypeService {
    VehicleType create(VehicleType vehicleType) throws Exception;
    List<VehicleType> fetchAll();
    VehicleType findById(Long id);
    VehicleType findByVehicleType(String vType);
}
