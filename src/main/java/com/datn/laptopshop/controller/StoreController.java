package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.dto.request.FilterProductRequest;
import com.datn.laptopshop.service.IBrandService;
import com.datn.laptopshop.service.ICategoryService;
import com.datn.laptopshop.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StoreController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IBrandService brandService;

    @Autowired
    private IProductService productService;

    @GetMapping("/store")
    public ResponseEntity<Object> showStore(
            @RequestParam(name = "sort",required = false) String sort,
            @RequestParam(name = "category",required = false) String categoryName,
            @RequestParam(name = "brand",required = false) String brandName,
            @RequestParam(name = "price",required = false) String price,
            @RequestParam(name = "page",defaultValue = "1") int page){
        FilterProductRequest filterProduct = new FilterProductRequest(sort,categoryName,brandName,price);
        int limit = 8;

        if (filterProduct.getBrandName() == null || filterProduct.getCateogryName() == null){
            filterProduct.setBrandName("all");
            filterProduct.setSort("all");
            filterProduct.setPrice("all");
            filterProduct.setCateogryName("all");
        }

        Page<ProductDto> listPageProduct = productService.findAll(filterProduct,page,limit);

        List<ProductDto> p = listPageProduct.getContent();

        Map m = new HashMap<>();
        m.put("brand", brandService.findAll());
        m.put("category", categoryService.findAll());
        m.put("totalPages", listPageProduct.getTotalPages());
        m.put("currentPage", page);
        m.put("listProduct",p);

        return ResponseHandler.responseBuilder("success","Get filter product success",HttpStatus.OK,m,0);
    }

    @PostMapping("/store")
    public ResponseEntity<Object> showStorePost(
            @RequestParam(name = "sort",required = false) String sort,
            @RequestParam(name = "category",required = false) String categoryName,
            @RequestParam(name = "brand",required = false) String brandName,
            @RequestParam(name = "price",required = false) String price,
            @RequestParam(name = "page",defaultValue = "1") int page){
        int limit = 8;
        FilterProductRequest filterProduct = new FilterProductRequest(sort,categoryName,brandName,price);

        System.out.println("page in post: "+page);
        if (filterProduct.getBrandName() == null || filterProduct.getCateogryName() == null){
            filterProduct.setBrandName("all");
            filterProduct.setSort("all");
            filterProduct.setPrice("all");
            filterProduct.setCateogryName("all");
        }

        Page<ProductDto> listPageProduct = productService.findAll(filterProduct,page,limit);

        List<ProductDto> p = listPageProduct.getContent();

        Map m = new HashMap<>();
        m.put("brand", brandService.findAll());
        m.put("category", categoryService.findAll());
        m.put("totalPages", listPageProduct.getTotalPages());
        m.put("currentPage", page);
        m.put("listProduct",p);

        return ResponseHandler.responseBuilder("success","Get filter product success",HttpStatus.OK,m,0);
    }

    @GetMapping("/store/{id}")
    public ResponseEntity<Object> getProductID(@PathVariable("id") long id){
        System.out.println("id cua product: "+id);
        ProductDto p = productService.findProductId(id);
        System.out.println("p: "+p);

        return ResponseHandler.responseBuilder("success","Get product by id success",HttpStatus.OK,p,0);
    }

}
