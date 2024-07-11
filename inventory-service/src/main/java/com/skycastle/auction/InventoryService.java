/*
 * Online auctioning system
 *
 * Copyright (c) 2022 - , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction;

import com.skycastle.auction.dto.User;
import com.skycastle.auction.entities.Statistics;
import com.skycastle.auction.entities.products.Category;
import com.skycastle.auction.entities.products.vehicles.VehicleBodyStyle;
import com.skycastle.auction.entities.products.vehicles.VehicleType;
import com.skycastle.auction.repositories.vehicles.VehicleBodyStyleRepository;
import com.skycastle.auction.services.stats.StatisticsService;
import com.skycastle.auction.services.vehicles.CategoryService;
import com.skycastle.auction.services.vehicles.VehicleTypeService;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@RequiredArgsConstructor
@Slf4j
public class InventoryService implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(InventoryService.class, args);
    }

    private final StatisticsService statisticsService;

    @Bean
    public RequestInterceptor requestInterceptor() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return requestTemplate -> {
                requestTemplate.header(AUTHORIZATION, user.getToken());
            };
        }
        return requestTemplate -> {
        };
    }

    //TODO automatically create default categories and vehicle types
    @Bean
    CommandLineRunner commandLineRunner(
            VehicleTypeService vehicleTypeService,
            CategoryService categoryService,
            VehicleBodyStyleRepository bodyStyleRepository
    ) {
        return args -> {
            try {
                VehicleType vehicleType = new VehicleType();
                vehicleType.setVehicleType("Automobiles");
                vehicleTypeService.create(vehicleType);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                // create categories
                Category category = new Category();
                category.setCategoryName("Vehicles");
                categoryService.create(category);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                // bodyTypes
                List<String> bodyTypes = Arrays.asList("4dr Spor", "Converti", "Coupe", "Hatchbac", "Sedan 4d", "Station");
                bodyTypes.forEach(style -> {
                    VehicleBodyStyle bodyStyle = new VehicleBodyStyle();
                    bodyStyle.setStyle(style);
                    bodyStyleRepository.save(bodyStyle);
                });
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        };
    }

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize stats table
        Statistics stat = statisticsService.findStatistics();
        if (stat == null) {
            // initialize one
            stat = new Statistics();
            statisticsService.save(stat);
        }

    }
}
