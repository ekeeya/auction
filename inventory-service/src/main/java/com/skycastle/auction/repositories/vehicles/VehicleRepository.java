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

package com.skycastle.auction.repositories.vehicles;

import com.skycastle.auction.entities.products.vehicles.Vehicle;
import com.skycastle.auction.entities.products.vehicles.VehicleModel;
import com.skycastle.auction.entities.products.vehicles.VehicleType;
import com.skycastle.auction.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query(value = "SELECT v FROM Vehicle v WHERE (v.isVisible=true) AND ((LOWER(v.name) LIKE %?1%) OR (LOWER(v.vehicleModel.model) LIKE %?1%)  OR (LOWER(v.vehicleModel.make) LIKE %?1%) OR (v.vin LIKE %?1%))")
    Page<Vehicle> complexSearch(String searchQuery, Pageable pageable);
    Page<Vehicle> findVehiclesBySellerAndIsVisible(Long sellerId,boolean visibility, Pageable pageable);
    Page<Vehicle> findVehiclesBySeller(Long sellerId, Pageable pageable);
    Page<Vehicle> findVehiclesByIsVisible(boolean visibility, Pageable pageable);
    Page<Vehicle> findVehiclesByVehicleTypeAndIsVisible(VehicleType vehicleType, boolean visibility, Pageable pageable);
    Page<Vehicle> findVehiclesByVehicleType(VehicleType vehicleType,  Pageable pageable);
    Page<Vehicle> findVehiclesByTransmissionAndIsVisible(Utils.TRANSMISSION transmission,boolean visible, Pageable pageable);
    Page<Vehicle> findVehiclesByOdometerBetweenAndIsVisible(Long min,Long max, boolean visibility, Pageable pageable);
    Page<Vehicle> findVehiclesByVehicleModel_YearBetweenAndIsVisible(Integer min, Integer max, boolean visibility, Pageable pageable);
    Page<Vehicle> findVehiclesByVehicleModelAndIsVisible(VehicleModel model, boolean visibility, Pageable pageable);
    Page<Vehicle> findVehiclesByVehicleModel_MakeAndIsVisible(String make,boolean visibility, Pageable pageable);
    Page<Vehicle> findVehiclesByBuyer(Long buyer, Pageable pageable);
    int countVehiclesByVehicleTypeAndIsVisible(VehicleType vehicleType, boolean visibility);
    int countVehiclesByVehicleModel_MakeAndIsVisible(String make, boolean visibility);
}
