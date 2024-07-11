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
import com.skycastle.auction.repositories.vehicles.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class VehicleTypeServiceImpl implements VehicleTypeService{
    private final VehicleTypeRepository vTypeRepository;

    @Override
    public VehicleType create(VehicleType vehicleType) throws Exception{
            VehicleType vType = vTypeRepository.findVehicleTypeByVehicleType(vehicleType.getVehicleType());
            if(vType != null){
                throw new DuplicateKeyException("Vehicle type %s already exists".formatted(vType.getVehicleType()));
            }
        return vTypeRepository.save(vehicleType);
    }

    @Override
    public List<VehicleType> fetchAll() {
        return vTypeRepository.findAll(Sort.by("vehicleType"));
    }

    @Override
    public VehicleType findById(Long id) {
        return vTypeRepository.findById(id).get();
    }

    @Override
    public VehicleType findByVehicleType(String vType) {
        return vTypeRepository.findVehicleTypeByVehicleType(vType);
    }
}
