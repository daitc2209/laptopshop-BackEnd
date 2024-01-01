package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.*;
import com.datn.laptopshop.dto.request.InforOrder;
import com.datn.laptopshop.entity.Order;
import com.datn.laptopshop.entity.OrderDetail;
import com.datn.laptopshop.enums.StateCheckout;
import com.datn.laptopshop.enums.StateOrder;
import com.datn.laptopshop.repos.*;
import com.datn.laptopshop.service.IOrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public OrderDto order(List<CartItem> carts, InforOrder inforOrder) {
        if (inforOrder == null || carts == null)
        {
            ResponseHandler.responseBuilder("Error","The input is null",
                    HttpStatus.BAD_REQUEST,"",99);
            return null;
        }

        var u = userRepository.findById(inforOrder.getUserId());
        if (u == null)
            return null;
        int delivery = 40000;
        Order o = new Order();
        o.setUser(u.get());
        o.setName(u.get().getFullname());
        o.setEmail(u.get().getEmail());
        o.setPhone(inforOrder.getPhone());
        o.setAddress_delivery(inforOrder.getAddress_delivery());
        o.setNum(inforOrder.getNum());
        o.setTotal_money(inforOrder.getTotalMoney() + delivery);
        o.setPayment(inforOrder.getPayment());
        o.setNote(inforOrder.getNote());
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
    public void updateStateCheckout(int id, StateCheckout paid) {
        Optional<Order> o = orderRepository.findById(id);
        if (o.isPresent()){
            o.get().setStateCheckout(paid);
            orderRepository.save(o.get());
        }
        else
            System.out.println("loi o phan update state check out");
    }

    @Override
    public OrderDto findById(int id) {
        var o = orderRepository.findById(id);
        if (o.isPresent())
            return new OrderDto().toOrderDto(o.get());
        return null;
    }

    @Override
    public List<OrderDto> findByOrderByStatus(String email, StateOrder status) {
        List<Order> listOrder = orderRepository.findByOrderByStatus(email, status);
        if (listOrder.isEmpty()){
                return new ArrayList<>();
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
    public List<OrderDto> findByOrderByStatus(StateOrder status) {
        List<Order> listOrder = orderRepository.findByOrderByStatus(status);
        if (listOrder.isEmpty()){
            return new ArrayList<>();
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
    public List<OrderDto> findOrderByRangeDay(String email, Date start, Date end, StateOrder status) {

        List<Order> listOrder = orderRepository.findOrderByRangeDay(email, start, end, status);

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
    public boolean deleteOrder(int id) {
        var o = orderRepository.findById(id);
        if (o.isEmpty())
            return false;
        orderRepository.delete(o.get());

        return true;
    }

    @Override
    public boolean cancelOrder(int id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty())
            return false;
        order.get().setStateOrder(StateOrder.CANCELLED);
        orderRepository.save(order.get());

        return true;
    }

    @Override
    public Page<OrderDto> findAll(int page, int limit, String search_text, StateOrder status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable p = PageRequest.of(page - 1,limit, sort);
        Page<Order> pageNews = orderRepository.findAll(search_text,status,p);

        if (pageNews.isEmpty())
            return null;

        Page<OrderDto> pageNewsDto =pageNews.map(n -> {
            OrderDto orderDto = new OrderDto().toOrderDto(n);
            List<OrderDetail> listOrderDetailEntity = n.getOrderdetail();
            List<OrderDetailDto> listOrderDetailDto = new ArrayList<>();
            for (OrderDetail o: listOrderDetailEntity
                 ) {
                OrderDetailDto oDto = new OrderDetailDto().toOrderDetailDto(o);
                oDto.setProduct(new ProductDto().toProductDTO(o.getProduct()));
                listOrderDetailDto.add(oDto);
            }
            orderDto.setOrderdetail(listOrderDetailDto);
            return orderDto;
        });

        return pageNewsDto;
    }

    @Override
    public Page<OrderDto> findOrderByRangeDay(int page, int limit, String search_text, Date start, Date end, StateOrder status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable p = PageRequest.of(page - 1,limit, sort);
        Page<Order> pageNews = orderRepository.findOrderByRangeDayAdmin(search_text, start, end,status,p);

        if (pageNews.isEmpty())
            return null;

        Page<OrderDto> pageNewsDto =pageNews.map(n -> {
            OrderDto orderDto = new OrderDto().toOrderDto(n);
            List<OrderDetail> listOrderDetailEntity = n.getOrderdetail();
            List<OrderDetailDto> listOrderDetailDto = new ArrayList<>();
            for (OrderDetail o: listOrderDetailEntity
            ) {
                OrderDetailDto oDto = new OrderDetailDto().toOrderDetailDto(o);
                oDto.setProduct(new ProductDto().toProductDTO(o.getProduct()));
                listOrderDetailDto.add(oDto);
            }
            orderDto.setOrderdetail(listOrderDetailDto);
            return orderDto;
        });

        return pageNewsDto;
    }

    @Override
    public boolean updateStateOrder(int id, StateOrder status) {

        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            return false;
        }

        // If saving modification fail, return false
        if (status == StateOrder.RECEIVED)
            order.get().setStateCheckout(StateCheckout.PAID);

        order.get().setStateOrder(status);
        if (orderRepository.save(order.get()) == null) {
            return false;
        }

        return true;
    }

}
