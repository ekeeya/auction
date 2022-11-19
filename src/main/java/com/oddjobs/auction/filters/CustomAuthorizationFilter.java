package com.oddjobs.auction.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oddjobs.auction.SpringContext;
import com.oddjobs.auction.services.TokenProviderService;
import com.oddjobs.auction.services.users.UserService;
import com.oddjobs.auction.utils.Utils;
import com.oddjobs.auction.utils.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {



    private  TokenProviderService tokenProviderService(){
        return SpringContext.getBean(TokenProviderService.class);
    };


    private  UserService userService(){
        return SpringContext.getBean(UserService.class);
    };


    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals("/login")){
            filterChain.doFilter(request, response);
        }else{
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                try{
                    String token = tokenProviderService().extractJwtFromRequest(request);
                    DecodedJWT decodedJWT =  tokenProviderService().validateToken(token);
                    String username =  decodedJWT.getSubject();
                    UserDetails userDetails = userService().loadUserByUsername(username);

                    Collection<? extends  GrantedAuthority> authorities = tokenProviderService().isAuthenticated(decodedJWT) ?
                             userDetails.getAuthorities() : List.of(new SimpleGrantedAuthority(Utils.ROLES.ROLE_PRE_VERIFIED.toString()));


                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    filterChain.doFilter(request, response);
                }catch (Exception exception){
                    log.error("Error logging in: {}", exception.getMessage());
                    response.setHeader("error", exception.getMessage());
                    response.setStatus(FORBIDDEN.value());
                    Map<String, String> errors=new HashMap<>();
                    errors.put("error", exception.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), errors);
                }
            }else{
                filterChain.doFilter(request, response);
            }
        }
    }
}
