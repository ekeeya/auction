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

package com.skycastle.auction.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycastle.auction.SpringContext;
import com.skycastle.auction.entities.tokens.RefreshToken;
import com.skycastle.auction.entities.users.User;
import com.skycastle.auction.services.TokenProviderService;
import com.skycastle.auction.services.refreshTokens.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private  final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
    }

    private TokenProviderService tokenProviderService(){
       return  SpringContext.getBean(TokenProviderService.class);
    }
    private RefreshTokenService  refreshTokenService(){
        return  SpringContext.getBean(RefreshTokenService.class);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username =  request.getParameter("username");
        String password =  request.getParameter("password");
        log.info("Attempted login by Username: {}", username);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        Object principal=  authentication.getPrincipal();
        User u = (User) principal;
        Map<String,Object>  accessToken = tokenProviderService().createToken(principal,!u.isUsing2FA(), false);


        Map<String, Map<String, Object>> tokens=new HashMap<>();
        tokens.put("access_token", accessToken);
        if(!u.isUsing2FA()){
            Map<String,Object>  refreshToken = tokenProviderService().createToken(principal, true, true);
            tokens.put("refresh_token", refreshToken);
            // store in database
            RefreshToken rToken = new RefreshToken();
            String refresh_token = (String) refreshToken.get("token");
            rToken.setToken(refresh_token);
            refreshTokenService().save(rToken);
        }

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
