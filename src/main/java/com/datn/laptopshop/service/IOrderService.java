package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.CartItem;
import com.datn.laptopshop.dto.NewsDto;
import com.datn.laptopshop.dto.OrderDto;
import com.datn.laptopshop.dto.request.InforOrder;
import com.datn.laptopshop.enums.StateCheckout;
import com.datn.laptopshop.enums.StateOrder;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IOrderService {
    OrderDto order(Collection<CartItem> carts, InforOrder inforOrder);

    OrderDto findByCodeOrder(String codeOrder);

    void updateStateCheckout(long id, StateCheckout paid);

    OrderDto findById(long id);

    List<OrderDto> findbyUser(String email);

    boolean cancelOrder(long id);

    Page<OrderDto> findAll(int page, int limit, String name, String payment, StateOrder stateOrder);

    boolean updateStateOrder(long id,StateOrder status);

    Page<OrderDto> revenue(int page, int limit, StateCheckout stateCheckout, StateOrder stateOrder, String payment, Date start, Date end);

}
