package com.skycastle.auction.services.vehicles;

import com.skycastle.auction.entities.products.vehicles.VehicleModel;
import com.skycastle.auction.repositories.vehicles.VehicleModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class VehicleModelServiceImpl implements VehicleModelService{

    private final VehicleModelRepository repository;

    @Override
    public List<VehicleModel> filterByMake(String make) {
        try{
            return repository.findVehicleModelByMake(make);
        }catch (Exception e){
            log.warn(e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public VehicleModel findById(Long id) {
        try{
            return repository.findById(id).get();
        }catch (NoSuchElementException e){
            return null;
        }
    }

    @Override
    public VehicleModel create(VehicleModel model) {
        VehicleModel  m =  repository.findVehicleModelByModel(model.getModel());
        if (m != null){
            throw new DuplicateKeyException("Vehicle Model already exists");
        }
        return repository.save(model);
    }
}
