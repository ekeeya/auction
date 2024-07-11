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

package com.skycastle.auction.dtos.responses;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class BaseResponseDTO implements Serializable {
    
    @Serial
    private static final long serialVersionUID = -8001795241473090377L;

    private boolean success = true;
    private String message;
    private Integer statusCode;
    private Object data;

    public BaseResponseDTO(BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            List<ObjectError> errors = bindingResult.getAllErrors();
           StringBuilder errorString = new StringBuilder();
            for (ObjectError e: errors
                 ) {
                if(e instanceof FieldError fe){
                    errorString.append("Field").append(fe.getField()).append(" ").append(fe.getDefaultMessage()).append(" ,");
                }
            }
            this.setMessage(errorString.toString());
            this.setSuccess(false);
            this.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }else{
            this.setStatusCode(HttpStatus.OK.value());
        }
    }
    public BaseResponseDTO() {
        this.setSuccess(true);
        this.setMessage("success");
        this.setStatusCode(200);
    }
    public BaseResponseDTO(Exception e) {
        this.setSuccess(false);
        this.setStatusCode(500);
        this.setMessage(e.getMessage());
    }
}
