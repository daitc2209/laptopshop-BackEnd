package com.datn.laptopshop.controller.Admin;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.request.SearchProductRequest;
import com.datn.laptopshop.dto.request.SearchUserRequest;
import com.datn.laptopshop.enums.StateOrder;
import com.datn.laptopshop.service.IOrderService;
import com.datn.laptopshop.service.IProductService;
import com.datn.laptopshop.service.IRevenueService;
import com.datn.laptopshop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/revenue")
@PreAuthorize("hasRole('ADMIN')")
public class RevenueController {

    @Autowired
    private IRevenueService revenueService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IProductService productService;

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

    @PostMapping("/range-day")
    public ResponseEntity<?> getRevenueFromRangeDay(@RequestBody Map<String, String> data ){
        try{
            String start = data.get("start");
            String end = data.get("end");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startDate = dateFormat.parse(start);
            Date endDate = dateFormat.parse(end);

            if (endDate.before(startDate))
                return ResponseHandler.responseBuilder("Error","startDate is greater than endDate !!!",
                        HttpStatus.BAD_REQUEST,"",99);

            Map m = new HashMap<>();
            var revenueDay = revenueService.revenueRangeDay(startDate, endDate);

            m.put("revenueDay", revenueDay);
            return ResponseHandler.responseBuilder("Success","get revenue Day successfully !!!",
                    HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("Error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @GetMapping("/card")
    public  ResponseEntity<?> getUserAndOrder(){
        Map m = new HashMap<>();
        SearchUserRequest search = new SearchUserRequest();
        search.setRole(2);
        var numUser = userService.findAll(1, 2,search);
        var numOrder = orderService.findByOrderByStatus(StateOrder.PENDING);
        var totalOrder = orderService.findByOrderByStatus(null);
        var totalProduct = productService.findAll(1,2, new SearchProductRequest("",-1,-1,0,0));
        m.put("numUser",numUser.getTotalElements());
        m.put("numOrder",numOrder.size());
        m.put("totalOrder",totalOrder.size());
        m.put("totalProduct",totalProduct.getTotalElements());

        return ResponseHandler.responseBuilder
                ("success", "Get list user success", HttpStatus.OK,m,0);
    }

//    @GetMapping("/order-by-month")
//    public ResponseEntity<?> getStatisticalByOrderWithMonth(){
//        try{
//            Map m = new HashMap<>();
//            var revenue = revenueService.statisticalByOrderWithMonth();
//            m.put("revenue", revenue);
//            return ResponseHandler.responseBuilder("Success","get revenue successfully !!!",
//                    HttpStatus.OK,m,0);
//        }
//        catch (Exception e){
//            return ResponseHandler.responseBuilder("Error",e.getMessage(),
//                    HttpStatus.BAD_REQUEST,"",99);
//        }
//    }
}
