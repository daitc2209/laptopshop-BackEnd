package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.BrandDto;

import java.util.List;

public interface IBrandService {

    List<BrandDto> findAll();

}
