/*
 * Online auctioning system
 *
 * Copyright (c) $today.year- , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.entities.users.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
public class ResponseHandler {
    private HttpStatus status;
    private boolean error=false;
    private String message;
    private Object data;
    private long count;
    private long totalPages;
    private long size;



    public static ResponseEntity<Object> generateResponse(ResponseHandler responseHandler){
        Map<String, Object> mapper = new HashMap<String, Object>();
        try{
            Object data =  responseHandler.getData();
            mapper.put("timestamp", new Date());
            mapper.put("status", responseHandler.getStatus().value());
            mapper.put("success", !responseHandler.isError());
            mapper.put("message", responseHandler.getMessage());
            mapper.put("data", data);
            if (data instanceof List && !responseHandler.isError()){
                mapper.put("count", responseHandler.getCount());
                mapper.put("size", responseHandler.getSize());
                mapper.put("totalPages", responseHandler.getTotalPages());
            }
            return new ResponseEntity<Object>(mapper, responseHandler.getStatus());

        }catch (Exception e){
            mapper.clear();
            mapper.put("timestamp", new Date());
            mapper.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            mapper.put("success",false);
            mapper.put("message", e.getMessage());
            mapper.put("data", null);
            return new ResponseEntity<Object>(mapper,responseHandler.getStatus());
        }
    }
}
