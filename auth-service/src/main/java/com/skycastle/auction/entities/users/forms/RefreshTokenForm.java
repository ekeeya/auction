/*
 * Online auctioning system
 *
 * Copyright (c)  $today.year- , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.entities.users.forms;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.ObjectError;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RefreshTokenForm extends BaseForm{

    @NotNull
    private String refreshToken;

    public RefreshTokenForm(List<ObjectError> errors){
        this.setErrors(errors);
    }
}
