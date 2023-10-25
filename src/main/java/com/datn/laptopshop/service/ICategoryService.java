package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.CategoryDto;

import java.util.List;

public interface ICategoryService {
    List<CategoryDto> findAll();
}
