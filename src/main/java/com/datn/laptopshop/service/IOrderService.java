package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.CartItem;
import com.datn.laptopshop.dto.OrderDto;
import com.datn.laptopshop.dto.request.InforOrder;
import com.datn.laptopshop.enums.StateCheckout;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IOrderService {
    OrderDto order(Collection<CartItem> carts, InforOrder inforOrder);

    OrderDto findByCodeOrder(String codeOrder);

    void updateStateCheckout(long id, StateCheckout paid);

    OrderDto findById(long id);

    List<OrderDto> findbyUser(String email);

    boolean cancelOrder(long id);
}
