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

package com.skycastle.auction;

import com.auth0.jwt.algorithms.Algorithm;
import com.skycastle.auction.entities.users.User;
import com.skycastle.auction.entities.users.forms.RegisterForm;
import com.skycastle.auction.repositories.UserRepository;
import com.skycastle.auction.services.users.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthenticationServer {
    @Value("${jwt.secret}")
    private String secret;
    public static void main(String[] args) {
        SpringApplication.run(AuthenticationServer.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    CommandLineRunner commandLineRunner(
            UserService userService
    ) {
        return args -> {
            User user = userService.findByUsername("anonymous");
            if(user == null){
                RegisterForm form =  new RegisterForm();
                form.setEmail("anonymous@skycastle.com");
                form.setUsername("anonymous");
                form.setAccountType("ANONYMOUS");
                form.setPassword("anonymous");
                form.setEnable2Fa(false);
                userService.registerUser(form);
            }
        };
    }

    @Bean
    public Algorithm algorithm(){
        assert secret != null;
        return  Algorithm.HMAC256(secret.getBytes());
    }
}
