package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.BrandDto;
import com.datn.laptopshop.dto.CategoryDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBrandService {

    List<BrandDto> findAll();

    Page<BrandDto> findAll(int page, int limit, String search);
    BrandDto findById(int id);
    boolean insert(BrandDto brandDto);

    boolean update(BrandDto brandDto);

    boolean delete(int id);
}
