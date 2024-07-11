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

package com.skycastle.auction.controllers;

import com.skycastle.auction.entities.forms.requests.CategoryRequestDTO;
import com.skycastle.auction.entities.forms.responses.BaseResponseDTO;
import com.skycastle.auction.entities.forms.responses.CategoryResponseDTO;
import com.skycastle.auction.entities.forms.responses.ListResponseDTO;
import com.skycastle.auction.entities.products.Category;
import com.skycastle.auction.services.vehicles.CategoryService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class CategoryController {


    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<ListResponseDTO<CategoryResponseDTO>> getAll(){
        ListResponseDTO<CategoryResponseDTO> response;
        try{
            List<CategoryResponseDTO> categories =  categoryService.findAll().stream().map(CategoryResponseDTO::new).collect(Collectors.toList());
            response = new ListResponseDTO<>(categories);
            response.setTotalCount(categories.size());
            response.setTotalPages(1);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            log.warn(e.getMessage());
            response =  new ListResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @RolesAllowed({"ROLE_ADMIN"})
    @PostMapping("/categories")
    public ResponseEntity<BaseResponseDTO> create(
            @RequestBody CategoryRequestDTO request, BindingResult bindingResult
            ){
        BaseResponseDTO response;
        List<BaseResponseDTO> entries = new ArrayList<>();
        try{
            response = new BaseResponseDTO(bindingResult);

            if (response.isSuccess()) {
                for(String categoryName : request.getCategoryNames()){
                    response = new BaseResponseDTO();
                    Category category = new Category();
                    category.setCategoryName(categoryName);
                    response.setData(new CategoryResponseDTO(categoryService.create(category)));
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setMessage("Successfully added product category "+ category.getCategoryName().toUpperCase());
                    entries.add(response);
                }
                return ResponseEntity.ok(new ListResponseDTO<>(entries));
            }
            return ResponseEntity.badRequest().body(response);
        }catch (Exception e){
            log.error(e.getMessage());
            response = new BaseResponseDTO(e);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
