package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SearchController {
    @Autowired
    private IProductService productService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("term") String term){
        Map m = new HashMap<>();
        List<ProductDto> listSearch = productService.findByNameSearch(term);
        m.put("listSearch",listSearch);
        return ResponseHandler.responseBuilder("success", "Search success", HttpStatus.OK,m,0);
    }
}
