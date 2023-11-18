package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.CategoryDto;
import com.datn.laptopshop.dto.NewsDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICategoryService {
    List<CategoryDto> findAll();

    Page<CategoryDto> findAll(int page, int limit, String search);
    CategoryDto findById(long id);
    boolean insert(CategoryDto categoryDto);

    boolean update(CategoryDto categoryDto);

    boolean delete(long id);
}
