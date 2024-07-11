/*
 * Online auctioning system
 *
 * Copyright (c) 2023.  Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.utils;

import com.skycastle.auction.dtos.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ContextProvider {
    public String getAuthorizationHeader(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  "Bearer %s".formatted(user.getToken());
    }

    public Collection<? extends GrantedAuthority> getAuthorities(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    public   User getPrincipal(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (User) principal;
    }

    public String getAuthHeaderValue(){
        User u = getPrincipal();
        return  "Bearer "+ u.getToken();
    }
}
