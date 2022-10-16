package com.oddjobs.auction.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {

    @RequestMapping("/home")
    public String home(){
        return"We are home";
    }

    @GetMapping("/dashboard")
    public String dashboard(){
        return "We are at the dashboard";
    }
}
