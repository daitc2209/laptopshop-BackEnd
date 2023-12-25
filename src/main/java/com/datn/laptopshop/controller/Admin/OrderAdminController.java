package com.datn.laptopshop.controller.Admin;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.OrderDto;
import com.datn.laptopshop.enums.StateOrder;
import com.datn.laptopshop.service.IOrderService;
import com.datn.laptopshop.utils.IdLogged;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/order")
@PreAuthorize("hasRole('ADMIN')")
public class OrderAdminController {

    @Autowired
    private IOrderService orderService;

    @GetMapping
    public ResponseEntity<?> orderPage(@RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "search_text", defaultValue = "") String search_text,
                                       @RequestParam(value = "status", defaultValue = "") String status){
        try {
            int limit = 6;
            Map m = new HashMap<>();

            StateOrder stateOrder;
            try{
                stateOrder = StateOrder.valueOf(status.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                stateOrder = null;
            }

            var listOrders = orderService.findAll(page,limit,search_text,stateOrder);
            if (listOrders != null){
                m.put("listOrders", listOrders.getContent());
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

    @PostMapping("/range-day")
    public ResponseEntity<?> getOrderByRangeDay(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestBody Map<String, String> data ){
        try {
            int limit = 6;
            Map m = new HashMap<>();
            String search_text = data.get("search_text");
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

            var listOrders = orderService.findOrderByRangeDay(page,limit,search_text, startDate, endDate,stateOrder);
            if (listOrders != null){
                m.put("listOrders", listOrders.getContent());
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

    @GetMapping("/{id}")
    public ResponseEntity<?> orderById(@PathVariable(name = "id") long id){
        try {
            Map m = new HashMap<>();
            var orders = orderService.findById(id);
            if (orders != null){
                m.put("orders", orders);
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

    @GetMapping("/total-order-status")
    public ResponseEntity<?> getTotalOrderReceived(){
        Map m = new HashMap<>();
        List<OrderDto> order_pending = orderService.findByOrderByStatus(StateOrder.PENDING);
        List<OrderDto> order_confirm = orderService.findByOrderByStatus(StateOrder.CONFIRMED);
        List<OrderDto> order_delivering = orderService.findByOrderByStatus(StateOrder.DELIVERING);
        List<OrderDto> order_received = orderService.findByOrderByStatus(StateOrder.RECEIVED);
        List<OrderDto> order_cancelled = orderService.findByOrderByStatus(StateOrder.CANCELLED);
        m.put("order_pending",order_pending.size());
        m.put("order_confirmed",order_confirm.size());
        m.put("order_delivering",order_delivering.size());
        m.put("order_received",order_received.size());
        m.put("order_cancelled",order_cancelled.size());
        return ResponseHandler.responseBuilder("Message","Cancel Success",
                HttpStatus.OK,m,0);
    }
}
