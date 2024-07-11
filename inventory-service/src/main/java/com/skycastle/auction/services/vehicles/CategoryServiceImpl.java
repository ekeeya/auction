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

package com.skycastle.auction.services.vehicles;

import com.skycastle.auction.entities.products.Category;
import com.skycastle.auction.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).get();
    }

    @Override
    public Category findByName(String name) {
        return categoryRepository.findCategoryByCategoryName(name);
    }

    @Override
    public Category create(Category category) {

        Category cat =  categoryRepository.findCategoryByCategoryName(category.getCategoryName());
        if (cat != null){
            String errorMsg = "Category with name %s already exists".formatted(category.getCategoryName());
            throw new DuplicateKeyException(errorMsg);
        }
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
