package com.oddjobs.auction.utils.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.oddjobs.auction.SpringContext;
import org.springframework.core.env.Environment;

public class JwtUtils {


    private static Environment env() {
        return SpringContext.getBean(Environment.class);
    }

    public static Algorithm getAlgorithm(){
        String secret = env().getProperty("jwt.secret");
        return  Algorithm.HMAC256(secret.getBytes());
    }
}
