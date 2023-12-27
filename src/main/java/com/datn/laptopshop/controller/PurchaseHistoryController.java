package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.OrderDto;
import com.datn.laptopshop.entity.Order;
import com.datn.laptopshop.enums.StateOrder;
import com.datn.laptopshop.service.IOrderService;
import com.datn.laptopshop.utils.IdLogged;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/purchase-history")
public class PurchaseHistoryController {

    @Autowired
    private IOrderService orderService;

    @GetMapping
    public ResponseEntity<Object> historyPage(@RequestParam(value = "status", defaultValue = "") String status) {
        try {
            Map m = new HashMap<>();
            StateOrder stateOrder;
            try{
                stateOrder = StateOrder.valueOf(status.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                stateOrder = null;
            }
            List<OrderDto> order = orderService.findByOrderByStatus(IdLogged.getUser(),stateOrder);
            m.put("order",order);
            return ResponseHandler.responseBuilder("Message","get Success",
                    HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("Error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Object> cancelOrder(@RequestParam("id") long id, @RequestParam("status") StateOrder status){
        Map m = new HashMap<>();
        boolean cancel = orderService.cancelOrder(id);
        List<OrderDto> order = orderService.findByOrderByStatus(IdLogged.getUser(), status);
        m.put("order",order);

        if (cancel)
            return ResponseHandler.responseBuilder("Message","Cancel Success",
                    HttpStatus.OK,m,0);

        return ResponseHandler.responseBuilder("Error","Bo qua that bai !!!",
                HttpStatus.BAD_REQUEST,"",99);
    }

    @PostMapping("/range-day")
    public ResponseEntity<?> getOrderByRangeDay(@RequestBody Map<String, String> data ){
        try{
            String start = data.get("start");
            String end = data.get("end");
            String status = data.get("status");
            Date startDate = null;
            Date endDate = null;
            // Kiem tra xem khoang ngay co bi null hay khong
            if (start != null || end != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                startDate = dateFormat.parse(start);
                endDate = dateFormat.parse(end);

                //Kiem tra xem ngay ket thuc co nho hon ngay bat dau hay khong
                if (endDate.before(startDate))
                    return ResponseHandler.responseBuilder("Error","startDate is greater than endDate !!!",
                            HttpStatus.BAD_REQUEST,"",99);
            }

            StateOrder stateOrder;
            try{
                stateOrder = StateOrder.valueOf(status.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                stateOrder = null;
            }

            Map m = new HashMap<>();
            var order = orderService.findOrderByRangeDay(IdLogged.getUser(),startDate, endDate, stateOrder);

            m.put("orderDay", order);
            return ResponseHandler.responseBuilder("Success","get revenue Day successfully !!!",
                    HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("Error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @GetMapping("/totalOrder")
    public ResponseEntity<?> getTotalOrderReceived(){
        int total_money=0;
        Map m = new HashMap<>();
        List<OrderDto> order = orderService.findByOrderByStatus(IdLogged.getUser(), StateOrder.RECEIVED);
//        List<OrderDto> order_delivering = orderService.findByOrderByStatus(IdLogged.getUser(), StateOrder.DELIVERING);
//        List<OrderDto> order_pending = orderService.findByOrderByStatus(IdLogged.getUser(), StateOrder.PENDING);
//        List<OrderDto> order_confirmed = orderService.findByOrderByStatus(IdLogged.getUser(), StateOrder.CONFIRMED);
        if (!order.isEmpty()) {
            for (OrderDto o : order) {
                total_money += o.getTotal_money();
            }
        }
//        m.put("total_order",(order.size() + order_confirmed.size() + order_pending.size()));
        m.put("total_order",order.size());
        m.put("total_money",total_money);
        return ResponseHandler.responseBuilder("Message","Get total order of user Success",
                HttpStatus.OK,m,0);
    }
}
