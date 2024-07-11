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

package com.skycastle.auction.entities.forms.responses;

import com.skycastle.auction.entities.LotEntity;
import com.skycastle.auction.entities.products.ProductImage;
import com.skycastle.auction.entities.products.vehicles.Vehicle;
import com.skycastle.auction.utils.Utils;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class VehicleResponseDTO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Map<String, String> properties = new HashMap<>();
    private Long seller;
    private Object sellerDetails;
    private String condition;
    private List<String> images;
    private LotDTO lot;
    private boolean isInspected = false;
    private String status = Utils.STATUS.IN_EVALUATION.toString();
    private String categoryName;
    private String vin;
    private Long odometer;
    private String model;
    private Long modelId;
    private String make;
    private String vehicleType;
    private Boolean buyNow;
    private BigDecimal sellingPrice=new BigDecimal(0);
    private BigDecimal reservePrice=new BigDecimal(0);
    private String bodyStyle;
    private String regNo;
    private String transmission;
    private String fuelType;

    public VehicleResponseDTO(Vehicle vehicle){
        this.setId(vehicle.getId());
        this.setName(vehicle.getName());
        this.setCategoryName(vehicle.getCategoryName());
        this.setDescription(vehicle.getDescription());
        this.setProperties(vehicle.getProperties());
        this.setSeller(vehicle.getSeller());
        this.setCondition(vehicle.getCondition().toString());
        this.setInspected(vehicle.isInspected());
        this.setStatus(vehicle.getStatus().toString());
        this.setVin(vehicle.getVin());
        this.setOdometer(vehicle.getOdometer());
        if (vehicle.getLot() != null){
            LotDTO lot =  new LotDTO(vehicle.getLot());
            setLot(lot);
        }
        this.setModelId(vehicle.getVehicleModel().getId());
        this.setModel(vehicle.getVehicleModel().getModel());
        this.setMake(vehicle.getVehicleModel().getMake());
        this.setVehicleType(vehicle.getVehicleType().getVehicleType());
        this.setBuyNow(vehicle.getBuyNow());
        this.setSellingPrice(vehicle.getSellingPrice());
        this.setReservePrice(vehicle.getReservePrice());
        this.setBodyStyle(vehicle.getBodyStyle().getStyle());
        this.setRegNo(vehicle.getRegNo());
        this.setTransmission(vehicle.getTransmission().toString());
        this.setFuelType(vehicle.getFuelType().toString());
        if(vehicle.getImages().size() > 0){
            List<String> images = new ArrayList<>();
            for(ProductImage productImage: vehicle.getImages()){
                images.add(productImage.getFileType()+","+productImage.getContent());
            }
            this.setImages(images);
        }
    }
}
