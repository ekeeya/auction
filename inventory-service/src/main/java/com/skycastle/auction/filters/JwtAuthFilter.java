/*
 * Online auctioning system
 *
 * Copyright (c) 2022.  Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
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
import com.skycastle.auction.clients.AuthClientStore;
import com.skycastle.auction.dto.User;
import com.skycastle.auction.utils.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try{
                String token = authorizationHeader.substring("Bearer ".length());
                Map<String, Object> u = (Map<String, Object>) redisTemplate.opsForValue().get(token);
                if (u ==  null){
                    throw new BadCredentialsException("Wrong or expired token");
                }
                User user =  new User();
                // If we ever want to use the token again, we can store it in auth principal
                user.setToken(token);

                Utils.setProperties(user, u);
                Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().toString()));
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }catch (Exception e){
                String error = "Authentication Error: "+ e.getMessage();
                log.error(error);
                response.setHeader("error", error);
                response.setStatus(UNAUTHORIZED.value());
                Map<String, String> errors=new HashMap<>();
                errors.put("error", error);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), errors);
            }
        }
        filterChain.doFilter(request, response);
    }
}
