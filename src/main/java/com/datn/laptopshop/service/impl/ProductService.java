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
        Sort sort = Sort.by(Sort.Direction.ASC, "price");
//        System.out.println("FilterProduct 1: "+filterProduct.toString());
        if (filterProduct.getBrandName().equals("all"))
            filterProduct.setBrandName("");
        if (filterProduct.getCateogryName().equals("all"))
            filterProduct.setCateogryName("");
        if (filterProduct.getSort().equals("high-low"))
            sort = Sort.by(Sort.Direction.DESC, "price");
        if (filterProduct.getSort().equals("a-z"))
            sort = Sort.by(Sort.Direction.ASC, "name");
        if (filterProduct.getSort().equals("z-a"))
            sort = Sort.by(Sort.Direction.DESC, "name");
        if (filterProduct.getPrice().equals("1-5"))
        {
            minPrice = 1000000;
            maxPrice = 5000000;
        }
        if (filterProduct.getPrice().equals("5-10"))
        {
            minPrice = 5000000;
            maxPrice = 10000000;
        }
        if (filterProduct.getPrice().equals("10-100"))
        {
            minPrice = 10000000;
            maxPrice = 100000000;
        }

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

    @Override
    public ProductDto findProductId(long id) {
        try{
            var product =  productRepository.findById(id);
            if (product.isPresent()){
                ProductDto dto = new ProductDto().toProductDTO(product.get());
                System.out.println("dto trong product id: "+dto);
                return dto;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
