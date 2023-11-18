package com.datn.laptopshop.controller.Admin;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.enums.StateCheckout;
import com.datn.laptopshop.enums.StateOrder;
import com.datn.laptopshop.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/order")
public class OrderAdminController {

    @Autowired
    private IOrderService orderService;

    @GetMapping
    public ResponseEntity<?> orderPage(@RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "name", defaultValue = "") String name,
                                       @RequestParam(value = "payment", defaultValue = "") String payment,
                                       @RequestParam(value = "status", defaultValue = "") StateOrder status){
        try {
            int limit = 6;
            Map m = new HashMap<>();
            var listOrders = orderService.findAll(page,limit,name,payment,status);
            if (listOrders != null){
                m.put("listOrders", listOrders);
                m.put("currentPage", page);
                m.put("totalPage", listOrders.getTotalPages());

            }
            return ResponseHandler.responseBuilder("message"," Get order Successfully",
                    HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("err",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }

    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam("id") long id,
                                    @RequestParam("status") StateOrder status) {
        try {
            boolean verify = orderService.updateStateOrder(id, status);

            if (verify) {
                return ResponseHandler.responseBuilder("message", "verify order Successfully !!",
                        HttpStatus.OK, "", 0);
            } else {
                return ResponseHandler.responseBuilder("error", "verify order Failed !!",
                        HttpStatus.OK, "", 0);
            }
        } catch (Exception e) {
            return ResponseHandler.responseBuilder("err", e.getMessage(),
                    HttpStatus.BAD_REQUEST, "", 99);
        }
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueByFilters(
            @RequestParam(name = "stateCheckout", defaultValue = "PAID",required = false) StateCheckout stateCheckout,
            @RequestParam(name = "stateOrder", defaultValue = "RECEIVED", required = false) StateOrder stateOrder,
            @RequestParam(name = "payment", defaultValue = "",required = false) String payment,
            @RequestParam(name = "start", defaultValue = "null",required = false) Date start,
            @RequestParam(name = "end", defaultValue = "null", required = false) Date end,
            @RequestParam(name = "page",defaultValue = "") int page
    ) {
        try{
            Map m = new HashMap<>();
            int limit = 10;
            var revenue = orderService.revenue(page, limit, stateCheckout,stateOrder,payment,start,end);

            m.put("revenue", revenue);
            m.put("currentPage",page);
            return ResponseHandler.responseBuilder("Success","get revenue successfully !!!",
                    HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("Error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

}