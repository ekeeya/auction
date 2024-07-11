/*
 * Online auctioning system
 * Copyright (C) 2023 - , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>
 *
 * This program is not free software
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 */

package com.skycastle.auction.controllers.buyer;

import com.skycastle.auction.entities.users.Buyer;
import com.skycastle.auction.services.users.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@ResponseBody
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BuyerController {

    private final UserService userService;

    @GetMapping("/buyer/{id}")
    public ResponseEntity<?> getBuyer(
            @PathVariable(name="id") Long sellerId
    ){
        try{
            Buyer buyer = (Buyer) userService.findUserById(sellerId);
            return ResponseEntity.ok(buyer);
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
