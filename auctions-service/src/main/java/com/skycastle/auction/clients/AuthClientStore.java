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

package com.skycastle.auction.clients;

import com.skycastle.auction.dtos.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "AUTH-SERVICE/api/v1")
public interface AuthClientStore {

    @RequestMapping(method=RequestMethod.GET, value = "/tokens/verify")
    User verifyClient(@RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue);

    @RequestMapping(method=RequestMethod.GET, value = "/buyer/{id}")
    User getBuyer(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String headerValue,
            @PathVariable(name="id") Long buyerId);
}