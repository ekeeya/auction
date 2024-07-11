/*
 * Online auctioning system
 *
 * Copyright (c) 2023.  Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.controllers;

import com.skycastle.auction.dtos.ApiUserResponseDTO;
import com.skycastle.auction.entities.forms.requests.MobileMoneyProductConfigDTO;
import com.skycastle.auction.entities.forms.responses.BaseResponseDTO;
import com.skycastle.auction.entities.mm.APIUser;
import com.skycastle.auction.repositories.mm.APIUserRepository;
import com.skycastle.auction.services.mm.MobileMoneyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class MMConfigController {

    private final MobileMoneyService mobileMoneyService;
    private final APIUserRepository apiUserRepository;

    //@RolesAllowed({"ROLE_ADMIN"})
    @PostMapping("mobile-money-config")
    public ResponseEntity<BaseResponseDTO> configureProduct(
            @RequestBody @Valid MobileMoneyProductConfigDTO request, BindingResult result){
        BaseResponseDTO response = new BaseResponseDTO(result);
        try{
            if(response.isSuccess()){
             Long id =  mobileMoneyService.configureMobileMoneyInTransaction(request);
             APIUser user =  apiUserRepository.findById(id).get();
             ApiUserResponseDTO apiUserResponseDTO =  new ApiUserResponseDTO(user);
             String message = String.format("Mobile money %s  product has been configured and API initialization", user.getProduct().getName());
             response.setMessage(message);
             response.setData(apiUserResponseDTO);
                log.info(String.valueOf(response));
                return  ResponseEntity.ok(response);
            }
            response.setData(result.getAllErrors());
            response.setSuccess(false);
            response.setMessage("Failure");
            log.warn(String.valueOf(response));
            return ResponseEntity.badRequest().body(response);
        }catch (Exception e){
            response =  new BaseResponseDTO(e);
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
