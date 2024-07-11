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

package com.skycastle.auction.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.skycastle.auction.entities.users.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenProviderService {

    @Value("${jwt.expires}")
    private int expires_in; // milliseconds

    private final Algorithm algorithm;


    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.issuer}")
    private String issuer;



   public Map<String, Object> createToken(Object principal, boolean authenticated, boolean isRefresh){

       User user = (User) principal;
       // Refresh token takes twice longer to expire
       Map<String, Object> tokenData = new HashMap<>();
       int expiresIn = isRefresh ? expires_in*2 : expires_in;
       Date createdAt = Calendar.getInstance().getTime();
       String token = JWT.create()
               .withSubject(user.getUsername())
               .withExpiresAt(new Date(System.currentTimeMillis()+ expiresIn))
               .withIssuer(issuer)
               .withClaim("AUTHENTICATED", authenticated)
               .withClaim("REFRESH", isRefresh)
               .sign(algorithm);
       // Store token in redis if it is authenticated and not refresh
       if (authenticated && !isRefresh){
           redisTemplate.opsForValue().set(token, user, expiresIn, TimeUnit.MILLISECONDS);
       }
       tokenData.put("token", token);
       tokenData.put("expiresIn", expiresIn);
       tokenData.put("createdAt", createdAt);
       return tokenData;
   }

   public String extractJwtFromRequest(HttpServletRequest request) throws Exception{
       String authorizationHeader = request.getHeader(AUTHORIZATION);
       if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
           return  authorizationHeader.substring("Bearer ".length());
       }
       throw new Exception("Wrong authorization header value");
   }
   public boolean isAuthenticated(DecodedJWT jwtToken){
       return jwtToken.getClaim("AUTHENTICATED").asBoolean();
   }

   public boolean isRefreshToken(String jwtToken) throws Exception {
       DecodedJWT token = validateToken(jwtToken);
       return token.getClaim("REFRESH").asBoolean();

   }

   public DecodedJWT validateToken(String token) throws Exception{
       JWTVerifier verifier = JWT.require(algorithm).build();
       return verifier.verify(token);
   }

}
