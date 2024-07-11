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

package com.skycastle.auction.mappers;

import com.skycastle.auction.entities.forms.responses.VehicleMakeResponseDTO;
import com.skycastle.auction.entities.forms.responses.VehicleTypeResponseDTO;
import com.skycastle.auction.entities.products.vehicles.VehicleType;
import com.skycastle.auction.services.vehicles.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Mapper {

    private final VehicleService  vehicleService;

    public VehicleTypeResponseDTO toVTypeDto(VehicleType vType){
        VehicleTypeResponseDTO dto = new VehicleTypeResponseDTO();
        dto.setId(vType.getId());
        dto.setVehicleType(vType.getVehicleType());
        dto.setCreatedAt(vType.getCreatedAt());
        dto.setVehicleCount(vehicleService.countByVehicleType(vType, true));
        return dto;
    }

    public VehicleMakeResponseDTO toVMakeDto(String  make){
        VehicleMakeResponseDTO  dto = new VehicleMakeResponseDTO();
        dto.setMake(make);
        dto.setVehicleCount(vehicleService.countByVehicleModelMake(make, true));
        return dto;
    }

}
