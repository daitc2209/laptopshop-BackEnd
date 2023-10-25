package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.dto.CategoryDto;
import com.datn.laptopshop.entity.Category;
import com.datn.laptopshop.repos.CategoryRepository;
import com.datn.laptopshop.service.ICategoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> findAll() {
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty()){
            return null;
        }
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : categories){
            CategoryDto categoryDto = new CategoryDto().toCateDto(category);
            categoryDtos.add(categoryDto);
        }
        return categoryDtos;
    }
}
