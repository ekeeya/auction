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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skycastle.auction.utils.Utils;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokensForm extends  BaseForm{

    public static enum ACTIONS{
        VERIFY(Values.VERIFY), REFRESH(Values.REFRESH);
        private ACTIONS (String val) {
            // force equality between name of enum instance, and value of constant
            if (!this.name().equals(val))
                throw new IllegalArgumentException("Incorrect use of ELanguage");
        }

        public static class Values {
            public static final String VERIFY= "verify";
            public static final String REFRESH= "refresh";
        }
    }

    private String code;
    private ACTIONS action = ACTIONS.VERIFY;
    private String refreshToken;

}
