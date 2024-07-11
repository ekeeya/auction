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

package com.skycastle.auction.components;

import com.skycastle.auction.clients.AuthClientStore;
import com.skycastle.auction.dtos.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomTokenAuthenticationManager implements AuthenticationManager {

    private final AuthClientStore authClientStore;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException{
        JWSAuthenticationToken token = (JWSAuthenticationToken) authentication;
        String tokenString = (String) token.getCredentials();
        try {
            List<GrantedAuthority> authorities;
            User user = authClientStore.verifyClient("Bearer "+tokenString);
            user.setToken(tokenString);
            authorities = List.of(new SimpleGrantedAuthority(user.getRole().toString()));
            token = new JWSAuthenticationToken(tokenString, user, authorities);
            token.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(token);
        } catch (Exception e) {
            log.debug("Exception authenticating the token {}:", tokenString, e);
            throw new BadCredentialsException("Invalid token");
        }

        return token;
    }
}
