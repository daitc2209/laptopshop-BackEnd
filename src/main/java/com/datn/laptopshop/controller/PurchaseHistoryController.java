package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.OrderDto;
import com.datn.laptopshop.entity.Order;
import com.datn.laptopshop.service.IOrderService;
import com.datn.laptopshop.utils.IdLogged;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PurchaseHistoryController {

    @Autowired
    private IOrderService orderService;

    @GetMapping("/purchase-history")
    public ResponseEntity<Object> historyPage() {
        try {
            Map m = new HashMap<>();

            List<OrderDto> order = orderService.findbyUser(IdLogged.getUser());
            m.put("order",order);
            if (order.isEmpty())
                return ResponseHandler.responseBuilder("Error","Khong tim thay !!!",
                        HttpStatus.BAD_REQUEST,"",99);

            return ResponseHandler.responseBuilder("Message","get Success",
                    HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("Error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/purchase-history/delete")
    public ResponseEntity<Object> cancelOrder(@RequestParam("id") long id){

        boolean cancel = orderService.cancelOrder(id);
        if (cancel)
            return ResponseHandler.responseBuilder("Message","get Success",
                    HttpStatus.OK,orderService.findById(id).getStateOrder(),0);

        return ResponseHandler.responseBuilder("Error","Bo qua that bai !!!",
                HttpStatus.BAD_REQUEST,"",99);
    }
}
