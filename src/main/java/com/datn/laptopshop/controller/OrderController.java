package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.CartItem;
import com.datn.laptopshop.dto.OrderDto;
import com.datn.laptopshop.dto.request.InforOrder;
import com.datn.laptopshop.dto.request.OrderRequest;
import com.datn.laptopshop.service.ICartService;
import com.datn.laptopshop.service.IOrderDetailService;
import com.datn.laptopshop.service.IOrderService;
import com.datn.laptopshop.service.IUserService;
import com.datn.laptopshop.utils.IdLogged;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private ICartService cartService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrderService orderService;

    @GetMapping("/order")
    public ResponseEntity<Object> getOrder(){
        try {
            Map m = new HashMap<>();
            if (cartService.isCartEmpty())
            {
                return ResponseHandler.responseBuilder("Error","Cart is empty",
                        HttpStatus.BAD_REQUEST,"",99);
            }
            else {
                m.put("email",userService.findUserByEmail(IdLogged.getUser()).getEmail());
                m.put("fullname",userService.findUserByEmail(IdLogged.getUser()).getFullname());
                m.put("listCart",cartService.getAllItems());
                m.put("totalQuantity",cartService.getTotalQuantity());
                m.put("totalMoney",cartService.getTotalMoney());
                m.put("orderDate",new Date());
                return ResponseHandler.responseBuilder("Message","get Success",
                        HttpStatus.OK,m,0);
            }
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("Error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/order")
    public ResponseEntity<Object> order(@RequestBody OrderRequest order){
        try {
            Map m = new HashMap<>();
            InforOrder inforOrder = new InforOrder();
            inforOrder.setUserId(userService.findUserByEmail(IdLogged.getUser()).getId());
            inforOrder.setName(userService.findUserByEmail(IdLogged.getUser()).getFullname());
            inforOrder.setNum(cartService.getTotalQuantity());
            inforOrder.setTotalMoney(cartService.getTotalMoney());

            inforOrder.setPhone(order.getPhone());
            inforOrder.setPayment(order.getTypePayment());
            inforOrder.setAddress_delivery(order.getAddress());

            Collection<CartItem> carts = cartService.getAllItems();
            OrderDto orderDto = orderService.order(carts, inforOrder);
            System.out.println("orderDto: "+orderDto.toString());
            System.out.println("type: "+order.getTypePayment());
            if (orderDto != null){
                cartService.clearItem();

                if (order.getTypePayment().equals("TRANSFER")){
                    String redirectUrl = "http://localhost:8080/api/checkout/vnpay?codeOrder=" + orderDto.getCodeOrder() + "&bankCode=" + order.getBankCode();

                    m.put("redirectUrl", redirectUrl);
                    return ResponseHandler.responseBuilder("Message","post order Success",
                            HttpStatus.OK,m,0);
                }
                else if (order.getTypePayment().equals("COD")){
                    System.out.println("thanh toan: COD");
                    String redirectUrl = "http://localhost:5173/bill";
                    m.put("redirectUrl", redirectUrl);
                    m.put("typePayment","COD");
                    m.put("orderId",orderDto.getId());
                    return ResponseHandler.responseBuilder("Message","post order Success",
                            HttpStatus.OK,m,0);
                }
            }

            return ResponseEntity.badRequest().build();

        }catch (Exception e){
            return ResponseHandler.responseBuilder("Error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

}