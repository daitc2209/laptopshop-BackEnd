package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BrandController {
    @Autowired
    private IBrandService brandService;
    @GetMapping("/findAllBrand")
    public ResponseEntity<?> findAll(){
        Map m = new HashMap<>();
        m.put("brands", brandService.findAll());
        return ResponseHandler.responseBuilder("success","get all Successfully !!!!!",
                HttpStatus.OK,m,0);
    }
}
