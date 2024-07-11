package com.skycastle.auction.services.vehicles;

import com.skycastle.auction.entities.products.vehicles.VehicleModel;

import java.util.List;

public interface VehicleModelService {

    List<VehicleModel> filterByMake(String make);
    VehicleModel findById(Long id);
    VehicleModel create(VehicleModel model);

}
