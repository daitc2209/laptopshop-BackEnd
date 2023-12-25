package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.CartItem;
import com.datn.laptopshop.dto.OrderDto;
import com.datn.laptopshop.dto.RevenueCategories;
import com.datn.laptopshop.dto.request.InforOrder;
import com.datn.laptopshop.enums.StateCheckout;
import com.datn.laptopshop.enums.StateOrder;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface IOrderService {
    OrderDto order(Collection<CartItem> carts, InforOrder inforOrder);

    OrderDto findByCodeOrder(String codeOrder);

    void updateStateCheckout(long id, StateCheckout paid);

    OrderDto findById(long id);

    List<OrderDto> findByOrderByStatus(String email,StateOrder status);

    List<OrderDto> findByOrderByStatus(StateOrder status);

    boolean cancelOrder(long id);

    Page<OrderDto> findAll(int page, int limit, String search_text, StateOrder status);

    Page<OrderDto> findOrderByRangeDay(int page, int limit, String search_text, Date start, Date end, StateOrder status);

    boolean updateStateOrder(long id,StateOrder status);

    List<OrderDto> findOrderByRangeDay(String email, Date start, Date end, StateOrder status);


}
