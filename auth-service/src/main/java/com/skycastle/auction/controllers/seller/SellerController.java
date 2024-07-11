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

package com.skycastle.auction.controllers.seller;


import com.skycastle.auction.entities.users.Seller;
import com.skycastle.auction.entities.users.dto.GenericUserDTO;
import com.skycastle.auction.entities.users.dto.Mapper;
import com.skycastle.auction.services.seller.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@ResponseBody
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final Mapper mapper;

    @GetMapping("/seller/{id}")
    public ResponseEntity<?> getSeller(
            @PathVariable(name="id") Long sellerId
    ){
        try{
            Seller seller = sellerService.findById(sellerId);
            GenericUserDTO user = mapper.toDTO(seller, null);
            return ResponseEntity.ok(user);
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/seller/{id}/approve")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<?> getSeller(
            @PathVariable(name="id") Long sellerId,
            @RequestParam(value="approved") boolean isApproved
    ){
        try{
            Seller seller = sellerService.approveSeller(sellerId, isApproved);
            Map<String, Object> response = new HashMap<>();
            response.put("approved", seller.getIsApproved());
            String approval =  seller.getIsApproved() ? "Approved" : "Rejected";
            response.put("message", "Seller %s has been %s for transacting on this platform".formatted(seller.getName(),approval));

            return ResponseEntity.ok(response);
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
