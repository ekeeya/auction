package com.skycastle.auction.clients;

import com.skycastle.auction.dto.AuctionPatchDTO;
import com.skycastle.auction.dto.AuctionRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="AUCTIONS-SERVICE/api/v1")
public interface AuctionClientStore {
    @RequestMapping(method = RequestMethod.POST, value = "/configure-auction-session")
    Object configureAuctionSession(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue,
            @RequestBody AuctionRequestDTO request);

    @RequestMapping(method = RequestMethod.POST, value = "/auction/{auctionId}")
    Object updateAuction(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue,
            @PathVariable(name="auctionId") Long auctionId,
            @RequestBody AuctionPatchDTO request);

    @RequestMapping(method = RequestMethod.GET, value = "/auction/product")
    Object getProductAuction(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue,
            @RequestParam(name = "productId") Long productId);

}
