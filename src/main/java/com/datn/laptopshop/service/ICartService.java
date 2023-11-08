package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.CartItem;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

public interface ICartService {
    ResponseEntity<Object> addItem(CartItem cartItem);
    ResponseEntity<Object> editItem(long id,int num);

    void removeItem(long id);

    void clearItem();

    CartItem findCartItem(long id);

    Collection<CartItem> getAllItems();

    int getTotalMoney();

    int getTotalQuantity();

    boolean isCartEmpty();
}
