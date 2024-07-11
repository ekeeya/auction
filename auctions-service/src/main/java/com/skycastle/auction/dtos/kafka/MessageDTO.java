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

package com.skycastle.auction.dtos.kafka;

import com.skycastle.auction.utils.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class MessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8001795241473090377L;
    private Object data;
    private String messageType;


    public MessageDTO(Exception e, Long buyer){
        setMessageType(Utils.MESSAGE_TYPES.ERROR.toString());
        Map<String, Object> data =  new HashMap<>();
        data.put("message", e.getMessage());
        data.put("buyer", buyer);
        setData(data);
    }

    public MessageDTO(Object data){
        setMessageType(Utils.MESSAGE_TYPES.SUCCESS.toString());
        setData(data);
    }
}
