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

package com.skycastle.auction.controllers;

import com.skycastle.auction.clients.AuctionClientStore;
import com.skycastle.auction.clients.AuthClientStore;
import com.skycastle.auction.components.ContextProvider;
import com.skycastle.auction.dto.AuctionRequestDTO;
import com.skycastle.auction.dto.UpdateResponse;
import com.skycastle.auction.dto.User;
import com.skycastle.auction.entities.LotEntity;
import com.skycastle.auction.entities.forms.requests.LotRequestDTO;
import com.skycastle.auction.entities.forms.requests.VehicleBodyStyleRequest;
import com.skycastle.auction.entities.forms.requests.VehicleRequestDTO;
import com.skycastle.auction.entities.forms.responses.BaseResponseDTO;
import com.skycastle.auction.entities.forms.responses.ListResponseDTO;
import com.skycastle.auction.entities.forms.responses.VehicleResponseDTO;
import com.skycastle.auction.entities.products.InspectionReport;
import com.skycastle.auction.entities.products.Product;
import com.skycastle.auction.entities.products.ProductImage;
import com.skycastle.auction.entities.products.vehicles.Vehicle;
import com.skycastle.auction.entities.products.vehicles.VehicleBodyStyle;
import com.skycastle.auction.entities.products.vehicles.VehicleModel;
import com.skycastle.auction.repositories.EntityRepository;
import com.skycastle.auction.repositories.LotRepository;
import com.skycastle.auction.repositories.vehicles.VehicleBodyStyleRepository;
import com.skycastle.auction.repositories.vehicles.VehicleModelRepository;
import com.skycastle.auction.services.CustomRunnable;
import com.skycastle.auction.services.IdentifiableRunnable;
import com.skycastle.auction.services.TransactionalExecutorService;
import com.skycastle.auction.services.stats.StatisticsService;
import com.skycastle.auction.services.vehicles.LotService;
import com.skycastle.auction.services.vehicles.VehicleService;
import com.skycastle.auction.utils.Utils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true, securedEnabled = true)
@Transactional
public class VehicleController {

    private final VehicleService vehicleService;
    private final EntityRepository entityRepository;
    private final VehicleBodyStyleRepository vehicleBodyStyleRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final AuthClientStore authClientStore;
    private final ContextProvider contextProvider;

    private final StatisticsService statisticsService;

    private final LotService lotService;
    private final LotRepository lotRepository;

    private final AuctionClientStore auctionClientStore;
    private final TransactionalExecutorService transactionalExecutorService;

    protected String getAuthorizationHeader() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "Bearer %s".formatted(user.getToken());
    }

    protected Collection<? extends GrantedAuthority> getAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    protected User getPrincipal() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("vehicle-search")
    public ResponseEntity<?> advancedVehicleSearch(
            @RequestParam(name = "odometers", required = false) List<Long> odometers,
            @RequestParam(name = "years", required = false) List<Integer> years,
            @RequestParam(name = "make", required = false) String make,
            @RequestParam(name = "model", required = false) Long modelId,
            @RequestParam(name = "newlyAdded", required = false) boolean newlyAdded,
            @RequestParam(name = "transmission", required = false) String transmission,
            @RequestParam(name = "fuelType", required = false) String fuelType,
            @RequestParam(name = "bodyStyle", required = false) String bodyStyle,
            @RequestParam(name = "saleDateRange", required = false) String saleDateRange,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        try {
            ListResponseDTO<VehicleResponseDTO> response;

            List<VehicleResponseDTO> vehiclesResponse;
            List<Vehicle> vehicles = new ArrayList<>();
            Page<Vehicle> vehiclePage;
            if (odometers == null && years == null && make == null && modelId == null && fuelType == null && transmission == null && bodyStyle == null && saleDateRange == null) {
                vehicles.addAll(vehicleService.findAll(offset, limit).getContent());
            }
            if (odometers != null) {
                Long min = odometers.get(0);
                Long max = odometers.get(1);
                vehicles = vehicleService.filterByOdometerRange(min, max, offset, limit).getContent();
            }
            if (years != null) {
                int min = years.get(0);
                int max = years.get(1);
                vehicles = vehicleService.filterVehiclesByYearRange(min, max, offset, limit).getContent();
            }
            if (make != null) {
                vehicles.addAll(vehicleService.filterVehiclesByMake(make, offset, limit).getContent());
            }
            if (modelId != null) {
                VehicleModel model = vehicleModelRepository.findVehicleModelById(modelId);
                if (model != null) {
                    vehicles = vehicleService.filterVehiclesByModel(model, offset, limit).getContent();
                }
            }
            if (transmission != null) {
                vehicles.addAll(vehicleService.filterVehiclesByTransmission(Utils.TRANSMISSION.valueOf(transmission), offset, limit).getContent());
            }

            /*if(fuelType != null){

            }
            if(bodyStyle != null){

            }
            if(saleDateRange != null){

            }*/
            // TODO you might want to remove duplicates
            vehiclePage = new PageImpl<>(vehicles, Pageable.unpaged(), vehicles.size());
            vehiclesResponse = vehicles.stream().map(VehicleResponseDTO::new).collect(Collectors.toList());
            response = new ListResponseDTO<>(vehiclesResponse);
            response.setTotalCount(vehiclePage.getTotalElements());
            response.setTotalPages(vehiclePage.getTotalPages());
            response.setPage(vehiclePage.getNumber());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    @GetMapping("/get-vehicles")
    public ResponseEntity<?> searchVehicles(
            @RequestParam(value = "queryString", required = false) String queryString,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "sellerId", required = false) Long sellerId,
            @RequestParam(name = "buyerId", required = false) Long buyerId,
            @RequestParam(name = "isPublished", required = false, defaultValue = "true") boolean isPublished
    ) {
        try {
            /**
             * If request is from seller only see their products
             * If from admin, see all but if seller is specified show only that
             * if from buyer show the latest from we shall later store cookies that we can read and return smart results
             * also buyer can smart search by
             * **/

            ListResponseDTO<VehicleResponseDTO> response;
            Page<Vehicle> vehiclePage;
            List<VehicleResponseDTO> vehicles;

            if (contextProvider.getRole().equals(Utils.ROLES.ROLE_SELLER) || sellerId != null) {
                sellerId = sellerId == null ? contextProvider.getPrincipal().getId() : sellerId; // just to be
                // seller can only view theirs
                // if admins specifies sellerId, this condition is true.
                //seller can filter by unpublished or published as well
                vehiclePage = vehicleService.findVehiclesBySellerAndVisibility(sellerId, isPublished, page, size);
            } else if (contextProvider.getRole().equals(Utils.ROLES.ROLE_ADMIN) && queryString == null) {
                // admin on top of the above can see everything
                vehiclePage = vehicleService.findAll(page, size);
            } else if (buyerId != null) {
                vehiclePage = vehicleService.findVehiclesByBuyer(buyerId, page,size);
            } else {
                // Buyers can search by a query string and/or see only published Vehicles
                if (queryString != null) {
                    vehiclePage = vehicleService.complexSearch(queryString.toLowerCase(), page, size);
                } else {
                    vehiclePage = vehicleService.findVehiclesByVisibility(true, page, size);
                }
            }
            vehicles = vehiclePage.getContent().stream().map(VehicleResponseDTO::new).collect(Collectors.toList());
            response = new ListResponseDTO<>(vehicles);
            response.setTotalCount(vehiclePage.getTotalElements());
            response.setTotalPages(vehiclePage.getTotalPages());
            response.setPage(vehiclePage.getNumber());
            return ResponseEntity.ok(response);
        } catch (Exception error) {
            log.trace(error.getMessage());
            return ResponseEntity.internalServerError().body(error.getMessage());
        }
    }

    @PostMapping("/vehicles")
    @Secured({"ROLE_SELLER", "ROLE_ADMIN"})
    public ResponseEntity<?> register(
            @RequestBody @Valid VehicleRequestDTO request, BindingResult result
    ) {
        BaseResponseDTO response = new BaseResponseDTO(result);

        try {
            if (response.isSuccess()) {
                Function<Long, Long> future = x -> {
                    try {
                        Map<String, Object> u = (Map<String, Object>) authClientStore.getSeller(contextProvider.getAuthHeaderValue(), request.getSeller());// this will fail if we have no such  seller
                        if (u == null) {
                            String msg = "We could not find a seller with ID: " + request.getSeller();
                            throw new RuntimeException(msg);
                        }
                        Number sellerId = (Number) u.get("id");
                        String sellerName = (String) u.get("name");
                        Vehicle vehicle = vehicleService.create(request);
                        // create statistics
                        statisticsService.updateStatistics(vehicle.getVehicleModel(), 0L, 1L);
                        // create Auction session as well
                        AuctionRequestDTO auctionRequest = new AuctionRequestDTO();
                        auctionRequest.setProduct(vehicle.getId());
                        auctionRequest.setSeller(sellerId.longValue());
                        auctionRequest.setSellerName(sellerName);
                        ProductImage productImage = vehicle.getImages().get(0);
                        String image = productImage.getFileType()+","+productImage.getContent();
                        auctionRequest.setProductImage(image);
                        auctionRequest.setName(String.format("%s Auction", vehicle.getName()));
                        auctionRequest.setReservePrice(vehicle.getReservePrice());
                        auctionRequest.setSellingPrice(vehicle.getSellingPrice());

                        Map<String, Object> r = (Map<String, Object>) auctionClientStore.configureAuctionSession(contextProvider.getAuthHeaderValue(), auctionRequest);
                        Map<String, Object> data = (Map<String, Object>) r.get("data");
                        Integer auctionId = (Integer) data.get("id");
                        vehicle.setAuctionId(auctionId.longValue());

                        return vehicle.getId();

                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                };
                IdentifiableRunnable customRunnable = new CustomRunnable(future);
                Long id = transactionalExecutorService.executeInTransaction(customRunnable); //returns vehicle Id
                Vehicle newVehicle = vehicleService.findById(id);
                VehicleResponseDTO vehicleResponseDTO = new VehicleResponseDTO(newVehicle);
                response.setData(vehicleResponseDTO);
                response.setMessage("Vehicle %s successfully registered!".formatted(newVehicle.getName()));
                return ResponseEntity.ok().body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/popular-vehicles")
    public ResponseEntity<?> getPopularVehicles(
            @RequestParam(name = "model", required = false) Long modeId,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        try {
            ListResponseDTO<VehicleResponseDTO> response;
            Page<Vehicle> vehiclePage;
            List<VehicleResponseDTO> vehicles;
            VehicleModel model;
            if (modeId != null) {
                model = vehicleModelRepository.findVehicleModelById(modeId);
            } else {
                model = statisticsService.findPopularModel();
            }
            if (model != null) {
                vehiclePage = vehicleService.filterVehiclesByModel(model, offset, limit);
            } else {
                vehiclePage = vehicleService.findVehiclesByVisibility(true, offset, limit);
            }
            vehicles = vehiclePage.getContent().stream().map(VehicleResponseDTO::new).collect(Collectors.toList());
            response = new ListResponseDTO<>(vehicles);
            response.setTotalCount(vehiclePage.getTotalElements());
            response.setTotalPages(vehiclePage.getTotalPages());
            response.setPage(vehiclePage.getNumber());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("statistics")
    public ResponseEntity<?> getStatistics() {
        return ResponseEntity.ok(statisticsService.findStatistics());
    }

    @PatchMapping("vehicles/{id}")
    @Secured({"ROLE_SELLER"})
    public ResponseEntity<BaseResponseDTO> partialUpdateVehicle(
            @RequestBody Map<String, Object> request,
            @PathVariable(name = "id") Long vehicleId
    ) {
        BaseResponseDTO response;
        try {
            response = new BaseResponseDTO();
            Vehicle vehicle = vehicleService.update(request, vehicleId, getPrincipal().getId());
            VehicleResponseDTO v = new VehicleResponseDTO(vehicle);
            response.setData(v);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response = new BaseResponseDTO(e);
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("vehicles/{id}")
    public ResponseEntity<BaseResponseDTO> getVehicle(
            @PathVariable(name = "id") Long vehicleId
    ) {
        BaseResponseDTO response;
        try {
            response = new BaseResponseDTO();
            VehicleResponseDTO vehicle = new VehicleResponseDTO(vehicleService.findById(vehicleId));
            Object seller =  authClientStore.getSeller(contextProvider.getAuthHeaderValue(),vehicle.getSeller());
            vehicle.setSellerDetails(seller);
            response.setData(vehicle);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response = new BaseResponseDTO(e);
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("vehicles/{id}/upload-inspection")
    @Secured({"ROLE_ADMIN"}) // only admins can upload inspection reports
    public ResponseEntity<BaseResponseDTO> uploadInspectionReport(
            @PathVariable(name = "id") Long vehicleId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        BaseResponseDTO response;
        try {
            Vehicle vehicle = vehicleService.findById(vehicleId);
            Function<Long, Long> future = x -> {
                InspectionReport report = new InspectionReport();
                try {
                    if (file != null) {
                        String contentType = file.getContentType();
                        assert contentType != null;
                        if (!contentType.equals(MediaType.APPLICATION_PDF_VALUE)) {
                            throw new Exception("File must be of of format PDF");
                        }
                        byte[] fileData = file.getBytes();
                        report.setContent(fileData);
                    }
                    report.setName(name);
                    report.setDescription(description);
                    vehicle.setInspected(true);
                    vehicle.setIsVisible(true);
                    vehicle.setInspectionReport(entityRepository.save(report));
                    entityRepository.save(vehicle);
                    // Add vehicle to new lot
                    LotEntity lotEntity;
                    // First pull out a pending lot belonging to this seller
                    lotEntity = lotRepository.findLotEntityBySellerAndStatus(vehicle.getSeller(), Utils.LOT_STATUSES.PENDING);
                    if (lotEntity == null) {
                        // create a new one
                        LotRequestDTO lotRequest = new LotRequestDTO();
                        lotRequest.setSeller(vehicle.getSeller());
                        lotEntity = lotService.createUpdateLot(lotRequest);
                    }
                    lotService.addProductToLot(lotEntity, vehicle);
                    return vehicle.getId();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            IdentifiableRunnable customRunnable = new CustomRunnable(future);
            Long id = transactionalExecutorService.executeInTransaction(customRunnable); // return vehicle id
            InspectionReport report = vehicleService.findById(id).getInspectionReport();
            report.setProduct(vehicle);
            response = new BaseResponseDTO();
            response.setStatusCode(HttpStatus.OK.value());
            response.setData(report);
            response.setMessage("Inspection report %s  has been uploaded successfully".formatted(name));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response = new BaseResponseDTO(e);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("vehicles/{id}/get-inspection")
    public HttpEntity<?> getInspectionReport(
            @PathVariable(name = "id") Long vehicleId
    ) {
        HttpHeaders header = new HttpHeaders();
        try {
            Vehicle vehicle = vehicleService.findById(vehicleId);
            InspectionReport report = vehicle.getInspectionReport();
            header.setContentType(MediaType.APPLICATION_PDF);
            header.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + report.getName().replace(" ", "_") + ".pdf");
            header.setContentLength(report.getContent().length);
            return new HttpEntity<>(report.getContent(), header);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/vehicles/{id}/seller")
    public ResponseEntity<BaseResponseDTO> getVehicleSeller(
            @PathVariable(name = "id") Long vehicleId
    ) {
        BaseResponseDTO response;
        try {
            Vehicle vehicle = vehicleService.findById(vehicleId);
            response = new BaseResponseDTO();
            Long sellerId = vehicle.getSeller();
            String authorizationToken = getAuthorizationHeader();
            Object seller = authClientStore.getSeller(authorizationToken, sellerId);
            response.setData(seller);
            response.setSuccess(true);
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            response = new BaseResponseDTO(e);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/vehicle-body-styles")
    public ResponseEntity<BaseResponseDTO> createVehicleMake(
            @RequestBody VehicleBodyStyleRequest request, BindingResult result
    ) {
        BaseResponseDTO response;
        List<BaseResponseDTO> entries = new ArrayList<>();

        try {
            response = new BaseResponseDTO(result);
            if (response.isSuccess()) {
                for (String style : request.getBodyTypes()) {
                    response = new BaseResponseDTO();
                    VehicleBodyStyle bodyStyle = new VehicleBodyStyle();
                    bodyStyle.setStyle(style);
                    response.setData(entityRepository.save(bodyStyle));
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setMessage("Successfully added a new vehicle body style " + bodyStyle.getStyle().toUpperCase());
                    entries.add(response);
                }
                return ResponseEntity.ok(new ListResponseDTO<>(entries));
            }
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response = new BaseResponseDTO(e);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/vehicle-body-styles")
    public ResponseEntity<ListResponseDTO<VehicleBodyStyle>> getAllBodyStyles() {
        List<VehicleBodyStyle> entries = vehicleBodyStyleRepository.findAll();
        return ResponseEntity.ok(new ListResponseDTO<>(entries));
    }

    @PostMapping("/create-lot")
    public ResponseEntity<?> createLot(
           @Valid @RequestBody LotRequestDTO request
    ) {
        try {
            LotEntity lotEntity = lotService.createUpdateLot(request);
            return ResponseEntity.ok(lotEntity);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/lot/add-remove/{lotId}")
    public ResponseEntity<?> addRemoveVehicle(
            @PathVariable(name = "lotId") Long lotId,
            @RequestParam(name = "vehicleId") Long vehicleId,
            @RequestParam(name = "add", defaultValue = "true") boolean add
    ) {
        try {
            Product vehicle = vehicleService.findById(vehicleId);
            LotEntity lot = lotService.findById(lotId);
            if (add) {
                lotService.addProductToLot(lot, vehicle);
            } else {
                lotService.removeProductFromLot(lot, vehicle);
            }
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("expire/lot/{lotId}")
    public ResponseEntity<?> expireLot(@PathVariable(name = "lotId") Long lotId) {
        try {
            LotEntity lot = lotService.findById(lotId);
            if (lot == null) {
                return ResponseEntity.badRequest().body("Lot Id: " + lotId + " does not exist");
            }
            LotEntity newLot = lotService.expireLot(lot);
            return ResponseEntity.ok(newLot);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("mark-sold/{vehicleId}")
    public ResponseEntity<?> markSold(
            @PathVariable(name="vehicleId") Long vehicleId,
            @RequestParam(name="buyer") Long buyer
    ){
        try{
            Vehicle vehicle = vehicleService.findById(vehicleId);
            vehicleService.markSold(vehicle, buyer);
            UpdateResponse response =  new UpdateResponse(true,String.format("%s has been sold", vehicle.getName()));
            return ResponseEntity.ok(response);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
