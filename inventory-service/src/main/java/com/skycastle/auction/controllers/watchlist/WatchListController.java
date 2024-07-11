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

package com.skycastle.auction.controllers.watchlist;

import com.skycastle.auction.entities.forms.responses.BaseResponseDTO;
import com.skycastle.auction.entities.forms.responses.ListResponseDTO;
import com.skycastle.auction.entities.forms.responses.VehicleResponseDTO;
import com.skycastle.auction.entities.products.vehicles.Vehicle;
import com.skycastle.auction.entities.watchlist.WatchList;
import com.skycastle.auction.services.watchlist.WatchlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class WatchListController {

    private final WatchlistService watchlistService;


    @GetMapping("/watchlist/add-remove")
    public ResponseEntity<?> getByBuyer(
            @RequestParam(name="buyerId") Long buyerId,
            @RequestParam(name="productId") List<Long> productIds,
            @RequestParam(name="action") String action
    ){
        BaseResponseDTO response;
       try{
           WatchList watchList;
           List<String> allowedActions = Arrays.asList("add", "remove");
           if (!allowedActions.contains(action)){
               return ResponseEntity.badRequest().body("Wrong value for parameter action. Must be either \"add\" or \"remove\"");
           }
           watchList = watchlistService.findByBuyerId(buyerId);
           if(watchList == null){
               // Create on for them
               WatchList newWatchlist = new WatchList();
               newWatchlist.setBuyerId(buyerId);
               watchList =  watchlistService.create(newWatchlist);
           }
           for(Long productId: productIds) {
               watchlistService.addRemove(productId, watchList.getId(), action.equals("add"));
           }
           String msg =  action.equals("add")? "Added":"Removed";
           response = new BaseResponseDTO();
           response.setStatusCode(200);
           response.setMessage(productIds.size()+ " have been %s from buyer's watchlist".formatted(msg));
           return ResponseEntity.ok(response);
       }catch (Exception e){
           log.error(e.getMessage());
           return ResponseEntity.internalServerError().body(e.getMessage());
       }
    }

    @GetMapping("watchlist/vehicles")
    public ResponseEntity<ListResponseDTO<VehicleResponseDTO>> getWatchlistProducts(
            @RequestParam(name="buyerId") Long buyerId
    ){
        ListResponseDTO<VehicleResponseDTO> response ;
        try{
            WatchList watchList = watchlistService.findByBuyerId(buyerId);
            List<VehicleResponseDTO> vehicles =  new ArrayList<>();
            if(watchList == null){
                response =   new ListResponseDTO<>(vehicles);
                return ResponseEntity.ok(response);
            }
            List<VehicleResponseDTO> products = watchlistService.getWatchListProducts(watchList.getId()).stream().map(p-> new VehicleResponseDTO((Vehicle) p)).collect(Collectors.toList());
            response =  new ListResponseDTO<>(products);
            return ResponseEntity.ok(response);

        }catch (Exception e){
            response = new ListResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}