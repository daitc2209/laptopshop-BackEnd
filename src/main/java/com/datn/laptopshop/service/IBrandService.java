package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.BrandDto;
import com.datn.laptopshop.dto.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBrandService {

    List<BrandDto> findAll();

    Page<BrandDto> findAll(int page, int limit, String search);
    BrandDto findById(int id);
    boolean insert(String name, MultipartFile img);

    boolean update(int id,String name, MultipartFile img);

    boolean delete(int id);
}
