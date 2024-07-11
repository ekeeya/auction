package com.skycastle.auction.clients;

import com.skycastle.auction.dtos.UpdateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "INVENTORY-SERVICE/api/v1")
public interface ProductClientStore {
    @RequestMapping(method= RequestMethod.GET, value = "/vehicles/{id}")
    Object getProduct(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue,
            @PathVariable(name = "id")Long vehicleId);

    @RequestMapping(method = RequestMethod.GET, value="mark-sold/{vehicleId}")
    UpdateResponse markSold(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue,
            @PathVariable(name = "vehicleId")Long vehicleId,
            @RequestParam(name = "buyer")Long buyerId);
}
