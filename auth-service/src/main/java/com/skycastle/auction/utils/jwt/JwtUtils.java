/*
 * Online auctioning system
 * Copyright (C) 2022 - , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *

 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>
 *
 * This program is not free software
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.utils.jwt;
import com.auth0.jwt.algorithms.Algorithm;
import com.skycastle.auction.SpringContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private  static Environment env() {
        return SpringContext.getBean(Environment.class);
    }

    public static Algorithm getAlgorithm(){
        String secret = env().getProperty("jwt.secret");
        assert secret != null;
        return  Algorithm.HMAC256(secret.getBytes());
    }
}
