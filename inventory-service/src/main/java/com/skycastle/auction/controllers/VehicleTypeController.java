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

import com.skycastle.auction.entities.forms.requests.BatchRequestDTO;
import com.skycastle.auction.entities.forms.requests.VehicleTypeRequestDTO;
import com.skycastle.auction.entities.forms.responses.BaseResponseDTO;
import com.skycastle.auction.entities.forms.responses.ListResponseDTO;
import com.skycastle.auction.entities.forms.responses.VehicleTypeResponseDTO;
import com.skycastle.auction.entities.products.vehicles.VehicleType;
import com.skycastle.auction.mappers.Mapper;
import com.skycastle.auction.services.vehicles.VehicleTypeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Produces;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@ResponseBody
@RequestMapping("/api/v1")
@Produces(MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VehicleTypeController {

    private final VehicleTypeService vehicleTypeService;
    private final Mapper mapper;


        @GetMapping("/vehicle-types")
    public ResponseEntity<ListResponseDTO<VehicleTypeResponseDTO>> getAll(){
        ListResponseDTO<VehicleTypeResponseDTO> response;
        try{
            List<VehicleTypeResponseDTO> vTypes = vehicleTypeService.fetchAll().stream().map(mapper::toVTypeDto).collect(Collectors.toList());
            response = new ListResponseDTO<>(vTypes);
            response.setTotalCount(vTypes.size());
            response.setTotalPages(1);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            log.warn(e.getMessage());
                     response =  new ListResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                    return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/vehicle-types")
    //@RolesAllowed({"ROLE_ADMIN"})
        public ResponseEntity<BaseResponseDTO> create(
            @RequestBody BatchRequestDTO<VehicleTypeRequestDTO> request
            ){

        List<BaseResponseDTO> entries = new ArrayList<>();
        BaseResponseDTO response;
        try{
            for(VehicleTypeRequestDTO r: request.getEntries()){
                try{
                    response =  new BaseResponseDTO();
                    VehicleType vehicleType =  new VehicleType();
                    vehicleType.setVehicleType(r.getVehicleType());
                    VehicleType newVType = vehicleTypeService.create(vehicleType);
                    response.setData(mapper.toVTypeDto(newVType));
                    response.setStatusCode(200);
                    response.setMessage("Vehicle type %s successfully saved".formatted(r.getVehicleType()));
                    entries.add(response);
                }catch (Exception e){
                    log.warn(e.getMessage());
                    response =  new BaseResponseDTO(e);
                    entries.add(response);
                }
            }
            return ResponseEntity.ok(new ListResponseDTO<>(entries));
        }catch (Exception e){
            log.warn(e.getMessage());
            response =  new BaseResponseDTO(e);
            return ResponseEntity.internalServerError().body(response);
        }


    }

}
