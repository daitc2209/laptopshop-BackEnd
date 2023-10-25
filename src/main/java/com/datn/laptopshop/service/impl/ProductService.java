package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.dto.request.FilterProductRequest;
import com.datn.laptopshop.entity.Category;
import com.datn.laptopshop.entity.Product;
import com.datn.laptopshop.repos.CategoryRepository;
import com.datn.laptopshop.repos.ProductRepository;
import com.datn.laptopshop.service.IProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProductService implements IProductService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Override
    public List<ProductDto> findByCategoryId(long id) {

        List<Product> listProduct = productRepository.findAllProductWithCategoryId(id);
        if(listProduct == null)
            return null;
        List<ProductDto> listProductDto = new ArrayList<>();
        for (Product product : listProduct) {
            System.out.println("product: "+ product);
            ProductDto dto = new ProductDto().toProductDTO(product);
            System.out.println("dto: "+ dto.toString());
            listProductDto.add(dto);
        }

        return listProductDto;
    }

    @Override
    public Page<ProductDto> findAll(FilterProductRequest filterProduct, int page, int limit) {
        double minPrice = 0;
        double maxPrice = 100000000;
//        System.out.println("FilterProduct 1: "+filterProduct.toString());
        if (filterProduct.getBrandName().equals("all"))
            filterProduct.setBrandName("");
        if (filterProduct.getCateogryName().equals("all"))
            filterProduct.setCateogryName("");
//        System.out.println("FilterProduct 2: "+filterProduct.toString());

        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Page<Product> product = productRepository.filterProduct(
                filterProduct.getBrandName(), filterProduct.getCateogryName(), minPrice, maxPrice, pageable);
        System.out.println("data tu repository: "+product.getContent());
        System.out.println("total page tu repository: "+product.getTotalPages());

        if(product == null)
            return null;
        List<ProductDto> listProductDto = new ArrayList<>();
        for (Product p : product.getContent()) {
            ProductDto dto = new ProductDto().toProductDTO(p);
            listProductDto.add(dto);
        }

        return new PageImpl<>(listProductDto, pageable, product.getTotalElements());
    }
}
