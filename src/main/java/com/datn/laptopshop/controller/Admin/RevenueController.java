package com.datn.laptopshop.controller.Admin;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.Revenue;
import com.datn.laptopshop.service.IRevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/revenue")
public class RevenueController {

    @Autowired
    private IRevenueService revenueService;

    @GetMapping("/categories")
    public ResponseEntity<?> getRevenueWithCategories() {
        try{
            Map m = new HashMap<>();
            var revenue = revenueService.revenueWithCategories();

            m.put("revenue", revenue);
            return ResponseHandler.responseBuilder("Success","get revenue categories successfully !!!",
                    HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("Error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @GetMapping
    public ResponseEntity<?> getRevenue(){
        try{
            Map m = new HashMap<>();
            var revenue = revenueService.revenue();
            m.put("revenue", revenue);
            return ResponseHandler.responseBuilder("Success","get revenue successfully !!!",
                    HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("Error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @GetMapping("/year")
    public ResponseEntity<?> getRevenueYear(@PathVariable(name = "year") String year){
        try{
            Map m = new HashMap<>();
            var revenue = revenueService.revenueYear(year);
            m.put("revenue", revenue);
            return ResponseHandler.responseBuilder("Success","get revenue years successfully !!!",
                    HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("Error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<?> getRevenueProduct(){
        try{
            Map m = new HashMap<>();
            var revenueProduct = revenueService.revenueProduct();

            m.put("revenueProduct", revenueProduct);
            return ResponseHandler.responseBuilder("Success","get revenue products successfully !!!",
                    HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("Error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }
}
