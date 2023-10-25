package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    IProductService productService;

    @GetMapping("/getAllProduct")
    public ResponseEntity<Object> getAllProduct(){
        Map m = new HashMap<>();
        m.put("laptop",productService.findByCategoryId(1));
        m.put("keyboard",productService.findByCategoryId(2));
        m.put("mouse",productService.findByCategoryId(3));
        return ResponseHandler.responseBuilder("Message","Success",
                HttpStatus.OK,m,1);
    }

}
