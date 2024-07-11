package com.skycastle.auction.components;

import com.skycastle.auction.dto.User;
import com.skycastle.auction.utils.Utils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContextProvider {
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    public Utils.ROLES getRole(){
        List<SimpleGrantedAuthority> roles = (List<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().toList();
        if(roles.size() >0){
            return  Utils.ROLES.valueOf(roles.get(0).toString());
        }
        return null;
    }

    public User getPrincipal(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public String getAuthHeaderValue(){
        User u = getPrincipal();
        return  "Bearer "+ u.getToken();
    }
}
