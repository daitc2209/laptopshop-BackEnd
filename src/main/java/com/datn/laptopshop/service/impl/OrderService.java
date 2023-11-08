package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.CartItem;
import com.datn.laptopshop.dto.OrderDetailDto;
import com.datn.laptopshop.dto.OrderDto;
import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.dto.request.InforOrder;
import com.datn.laptopshop.entity.Order;
import com.datn.laptopshop.entity.OrderDetail;
import com.datn.laptopshop.enums.StateCheckout;
import com.datn.laptopshop.enums.StateOrder;
import com.datn.laptopshop.repos.OrderDetailRepository;
import com.datn.laptopshop.repos.OrderRepository;
import com.datn.laptopshop.repos.ProductRepository;
import com.datn.laptopshop.repos.UserRepository;
import com.datn.laptopshop.service.IOrderService;
import com.datn.laptopshop.service.IUserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class OrderService implements IOrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public OrderDto order(Collection<CartItem> carts, InforOrder inforOrder) {
        if (inforOrder == null || carts == null)
        {
            ResponseHandler.responseBuilder("Error","The input is null",
                    HttpStatus.BAD_REQUEST,"",99);
            return null;
        }

        var u = userRepository.findById(inforOrder.getUserId());
        if (u == null)
            return null;

        Order o = new Order();
        o.setUser(u.get());
        o.setName(u.get().getFullname());
        o.setEmail(u.get().getEmail());
        o.setPhone(inforOrder.getPhone());
        o.setAddress_delivery(inforOrder.getAddress_delivery());
        o.setNum(inforOrder.getNum());
        o.setTotal_money(inforOrder.getTotalMoney());
        o.setPayment(inforOrder.getPayment());
        o.setCreated_at(new Date());
        o.setStateCheckout(StateCheckout.UNPAID);
        o.setStateOrder(StateOrder.PENDING);
        Order order = orderRepository.save(o);

        System.out.println("order id trong orderService: "+order.getId());

        for (CartItem cartItem : carts){
            OrderDetail od =new OrderDetail();
            od.setOrder(order);
            od.setProduct(productRepository.findById(cartItem.getProductId()).get());
            od.setPrice(cartItem.getPrice());
            od.setDiscount(cartItem.getDiscount());
            od.setNum(cartItem.getNumProduct());
            od.setTotalPrice(cartItem.getTotalPrice());
            orderDetailRepository.save(od);
        }

        // Save order code. vd: (orderId + userId + orderDate) = codeOrder
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = formatter.format(order.getCreated_at());
        String codeOrder = "" + order.getId() + order.getUser().getId() + date;
        order.setCodeOrder(codeOrder);
        System.out.println("codeOrder id trong orderService: "+codeOrder);
        orderRepository.save(order);

        return new OrderDto().toOrderDto(order);
    }

    @Override
    public OrderDto findByCodeOrder(String codeOrder) {
        var o = orderRepository.findByCodeOrder(codeOrder);
        if (o.isPresent())
            return new OrderDto().toOrderDto(o.get());
        return null;
    }

    @Override
    public void updateStateCheckout(long id, StateCheckout paid) {
        Optional<Order> o = orderRepository.findById(id);
        if (o.isPresent()){
            o.get().setStateCheckout(paid);
            orderRepository.save(o.get());
        }
        else
            System.out.println("loi o phan update state check out");
    }

    @Override
    public OrderDto findById(long id) {
        var o = orderRepository.findById(id);
        if (o.isPresent())
            return new OrderDto().toOrderDto(o.get());
        return null;
    }

    @Override
    public List<OrderDto> findbyUser(String email) {

        List<Order> listOrder = orderRepository.findByUser(email);
        if (listOrder.isEmpty()){
            return null;
        }

        List<OrderDto> listOrderDto = new ArrayList<>();
        for (Order o : listOrder){
            OrderDto orderDto = new OrderDto().toOrderDto(o);
            List<OrderDetail> listOrderDetail = o.getOrderdetail();
            List<OrderDetailDto> listOrderDetailDto = new ArrayList<>();
            for (OrderDetail oEntity : listOrderDetail){
                OrderDetailDto oDto = new OrderDetailDto().toOrderDetailDto(oEntity);
                oDto.setProduct(new ProductDto().toProductDTO(oEntity.getProduct()));
                listOrderDetailDto.add(oDto);
            }

            orderDto.setOrderdetail(listOrderDetailDto);

            listOrderDto.add(orderDto);
        }


        return listOrderDto;
    }

    @Override
    public boolean cancelOrder(long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty())
            return false;
        order.get().setStateOrder(StateOrder.CANCELLED);
        orderRepository.save(order.get());

        return true;
    }
}