package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.dto.request.FilterProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IProductService {
    List<ProductDto> findByCategoryId(long id);

    Page<ProductDto> findAll(FilterProductRequest filterProduct, int page, int limit);

}
