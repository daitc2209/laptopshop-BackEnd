package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.OrderDetailDto;
import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.dto.request.FilterProductRequest;
import com.datn.laptopshop.dto.request.SearchProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {
    List<ProductDto> findByCategoryId(int id);
    List<ProductDto> findByCategoryName(String name);
    Page<ProductDto> findAll(FilterProductRequest filterProduct, int page, int limit);

    ProductDto findProductId(int id);

    List<ProductDto> findByNameSearch(String term);

    Page<ProductDto> findAll(int page, int limit, SearchProductRequest search);

    boolean insert(ProductDto productDto, MultipartFile fileImage);
    boolean update(ProductDto productDto, MultipartFile fileImage);
    boolean delete(int id);

    boolean updateQuantityProduct(List<OrderDetailDto> list);

    boolean stateProduct(int id, int state);

}
