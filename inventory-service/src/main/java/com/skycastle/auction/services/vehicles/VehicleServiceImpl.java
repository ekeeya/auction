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

import com.skycastle.auction.components.ContextProvider;
import com.skycastle.auction.dto.User;
import com.skycastle.auction.entities.forms.requests.VehicleRequestDTO;
import com.skycastle.auction.entities.products.Category;
import com.skycastle.auction.entities.products.ProductImage;
import com.skycastle.auction.entities.products.vehicles.*;
import com.skycastle.auction.exceptions.ResourceForbiddenException;
import com.skycastle.auction.exceptions.VehicleNotFoundException;
import com.skycastle.auction.repositories.EntityRepository;
import com.skycastle.auction.repositories.vehicles.VehicleBodyStyleRepository;
import com.skycastle.auction.repositories.vehicles.VehicleRepository;
import com.skycastle.auction.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RequiredArgsConstructor
@Service
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final EntityRepository entityRepository;
    private final CategoryService  categoryService;
    private final VehicleModelService modelService;
    private final VehicleTypeService vehicleTypeService;
    private final VehicleBodyStyleRepository  vehicleBodyStyleRepository;

    private final ContextProvider contextProvider;


    protected List<ProductImage> generateImages(List<String> images, String name){
        List<ProductImage> productImages = new ArrayList<>();
        for (String base64String : images) {
            ProductImage image = new ProductImage();
            String imgName = name + "-" + System.currentTimeMillis();
            String[] strings = base64String.split(",");
            image.setName(imgName);
            image.setFileType(strings[0]);
            image.setContent(strings[1]);
            productImages.add(entityRepository.save(image));
        }
        return productImages;
    }

    protected Vehicle setRelationships(Vehicle vehicle, Long categoryId, Long modelId, Long VehicleTypeId, List<String> images, Long vehicleBodyId){
        if (categoryId != null){
            // Set category
            Category category = categoryService.findById(categoryId);
            vehicle.setCategory(category);
        }
        if(modelId !=null){
            // set Model
            VehicleModel model = modelService.findById(modelId);
            vehicle.setVehicleModel(model);
        }

        if(vehicleBodyId !=null){
            // set body type
            VehicleBodyStyle vehicleBodyStyle =  vehicleBodyStyleRepository.findById(vehicleBodyId).get();
            vehicle.setBodyStyle(vehicleBodyStyle);
        }

        if(VehicleTypeId !=null){
            // Set vehicle Type
            VehicleType vehicleType = vehicleTypeService.findById(VehicleTypeId);
            vehicle.setVehicleType(vehicleType);
        }
        if (images != null){
            if (images.size() > 0){
                // Lets delete old images first
                if(vehicle.getId() != null && vehicle.getImages() != null){
                    entityRepository.deleteAll(vehicle.getImages());
                }
                List<ProductImage> productImages = generateImages(images, vehicle.getName());
                vehicle.setImages(productImages);
            }
        }
        return vehicle;
    }

    @Override
    public Vehicle create(VehicleRequestDTO request) throws Exception {

        Category category = categoryService.findByName("Vehicles"); // This must be there
        if (category == null){
            throw new Exception("Category Vehicles does not exist, please first create on by POST on /categories with a list of categories e.g. [\"Vehicles\"]");
        }
        Long modelId = request.getModel();
        Long vehicleTypeId =request.getVehicleType();
        List<String> images = request.getImages();
        Vehicle vehicle;
        vehicle = new Vehicle();
        vehicle.setName(request.getName());
        vehicle.setProperties(request.getProperties());
        vehicle.setOdometer(request.getOdometer());
        vehicle.setVin(request.getVin());
        vehicle.setRegNo(request.getRegNo());
        vehicle.setCondition(request.getCondition());
        vehicle.setDescription(request.getDescription());
        vehicle.setSeller(request.getSeller());
        vehicle.setMinBidAmount(request.getMinBidAmount());
        vehicle.setInspected(request.getIsInspected());
        vehicle.setTransmission(Utils.TRANSMISSION.valueOf(request.getTransmission()));
        vehicle.setFuelType(Utils.FUEL_TYPES.valueOf(request.getFuelType()));
        vehicle.setBuyNow(request.getBuyNow());
        if (request.getBuyNow()) {
            vehicle.setSellingPrice(request.getSellingPrice());
        }
        vehicle.setReservePrice(request.getReservePrice());
        vehicle = setRelationships(vehicle,category.getId(),modelId,vehicleTypeId,images,request.getBodyStyle());
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle update(Map<String, Object> request, Long id, Long userid) throws IllegalAccessException {
        // this will throw an exception if such a vehicle does not exist.

        Vehicle vehicle;
        vehicle = vehicleRepository.findById(id).get();
        if(!vehicle.getSeller().equals(userid)){
            throw new IllegalAccessException("You are not allowed to edit this vehicle details");
        }
        Number modelId = (Number) request.getOrDefault("model", null);
        Number vehicleTypeId = (Number) request.getOrDefault("vehicleType", null);
        Number vehicleBodyStyleId = (Number) request.getOrDefault("bodyStyle", null);
        String condition = (String) request.getOrDefault("condition", null);
        List<String> images = (List<String>) request.getOrDefault("images", null);
        Long model =  modelId != null ? modelId.longValue() : null;
        Long vehicleType = vehicleTypeId != null ?vehicleTypeId.longValue() : null;
        Long bodyStyle = vehicleBodyStyleId != null ?vehicleBodyStyleId.longValue() : null;

        vehicle = setRelationships(vehicle,null,model,vehicleType,images, bodyStyle);
        if (condition != null){
            vehicle.setCondition(Utils.PRODUCT_CONDITION.valueOf(condition));
        }
        request.remove("model");
        request.remove("vehicleType");
        request.remove("images");
        request.remove("condition");

        Vehicle newVehicle = (Vehicle) Utils.setProperties(vehicle,request);
        return vehicleRepository.save(newVehicle);

    }

    @Override
    public Page<Vehicle> complexSearch(String searchQuery, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return vehicleRepository.complexSearch(searchQuery, pageable);
    }


    @Override
    public Page<Vehicle> findAll(int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return vehicleRepository.findAll(pageable);
    }

    @Override
    public Page<Vehicle> findVehiclesBySeller(Long sellerId, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return vehicleRepository.findVehiclesBySeller(sellerId, pageable);
    }

    @Override
    public Page<Vehicle> findVehiclesBySellerAndVisibility(Long sellerId, Boolean visibility, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        if (visibility == null){
            return findVehiclesBySeller(sellerId, page, size);
        }
        return vehicleRepository.findVehiclesBySellerAndIsVisible(sellerId,visibility, pageable);
    }

    @Override
    public Page<Vehicle> findVehiclesByVisibility(Boolean visibility, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        if(visibility == null){
            return findAll(page, size);
        }
        return vehicleRepository.findVehiclesByIsVisible(visibility, pageable);
    }

    @Override
    public Page<Vehicle> filterByOdometerRange(Long min, Long max, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return vehicleRepository.findVehiclesByOdometerBetweenAndIsVisible(min-1, max+1, true, pageable);
    }

    @Override
    public Page<Vehicle> filterVehiclesByYearRange(int min, int max, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return vehicleRepository.findVehiclesByVehicleModel_YearBetweenAndIsVisible(min, max, true, pageable);
    }

    @Override
    public Page<Vehicle> filterVehiclesByMake(String make, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return vehicleRepository.findVehiclesByVehicleModel_MakeAndIsVisible(make, true, pageable);
    }

    @Override
    public Page<Vehicle> filterVehiclesByModel(VehicleModel model, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return vehicleRepository.findVehiclesByVehicleModelAndIsVisible(model, true, pageable);
    }

    @Override
    public Page<Vehicle> filterVehiclesByTransmission(Utils.TRANSMISSION transmission, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return vehicleRepository.findVehiclesByTransmissionAndIsVisible(transmission,true, pageable);
    }

    @Override
    public Page<Vehicle> findVehiclesByBuyer(Long buyer, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        return vehicleRepository.findVehiclesByBuyer(buyer, pageable);
    }

    @Override
    public void markSold(Vehicle vehicle, Long buyer) {
        vehicle.setStatus(Utils.STATUS.SOLD);
        vehicle.setIsVisible(false); // won't show up on the list
        vehicle.setBuyer(buyer);
        vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle findById(Long id) throws Exception{
        if (vehicleRepository.findById(id).isEmpty()){
            throw new VehicleNotFoundException("Vehicle with id %d does not exist".formatted(id));
        }
        Vehicle vehicle = vehicleRepository.findById(id).get();
        User u =  contextProvider.getPrincipal();
        if (u.getRole().equals(Utils.ROLES.ROLE_SELLER) && !Objects.equals(u.getId(), vehicle.getSeller())){
            throw new ResourceForbiddenException();
        }
        return vehicle;
    }

    @Override
    public Page<Vehicle> findVehiclesByVehicleTypeAndAndVisibility(VehicleType vehicleType, Boolean visibility, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        if (visibility == null){
            return vehicleRepository.findVehiclesByVehicleType(vehicleType, pageable);
        }
        return vehicleRepository.findVehiclesByVehicleTypeAndIsVisible(vehicleType, visibility, pageable);
    }

    @Override
    public int countByVehicleType(VehicleType vehicleType, boolean visibility) {
        return vehicleRepository.countVehiclesByVehicleTypeAndIsVisible(vehicleType, visibility);
    }

    @Override
    public int countByVehicleModelMake(String make, boolean visibility) {
        return vehicleRepository.countVehiclesByVehicleModel_MakeAndIsVisible(make, visibility);
    }

}
