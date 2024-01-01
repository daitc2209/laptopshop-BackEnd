package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.CartItem;
import com.datn.laptopshop.repos.ProductRepository;
import com.datn.laptopshop.repos.UserRepository;
import com.datn.laptopshop.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService implements ICartService {

    private Map<Integer, CartItem> carts = new HashMap<>();

    @Override
    public ResponseEntity<Object> addItem(CartItem item) {
        CartItem cartItem = carts.get(item.getProductId());
        System.out.println("vao duoc additem Service");
        System.out.println("Them san pham: "+item.toString());
        if (cartItem == null) {
            System.out.println("vao duoc additem Service: khong co item trong cart");
            carts.put(item.getProductId(), item);
        } else {
            System.out.println("vao duoc addItem Service: co item trong cart");
            cartItem.setNumProduct(cartItem.getNumProduct() + item.getNumProduct());
            cartItem.setTotalPrice(item.getTotalPrice() + cartItem.getTotalPrice());
        }

        return ResponseHandler.responseBuilder("success","Get product by id success", HttpStatus.OK,"",0);
    }

    @Override
    public ResponseEntity<Object> editItem(int id, int num) {
        CartItem cartItem = carts.get(id);
        System.out.println("edit Item Service");
        System.out.println("Them san pham: "+cartItem.toString());
        if (cartItem == null ){
            return ResponseHandler.responseBuilder("Error","Item not in cart", HttpStatus.BAD_REQUEST,"",99);
        }
        cartItem.setNumProduct(num);
        cartItem.setTotalPrice(num*(cartItem.getPrice()-(cartItem.getPrice()*cartItem.getDiscount()/100)));
        System.out.println("tong gia: "+cartItem.getTotalPrice());
        System.out.println("so luong: "+cartItem.getNumProduct());
        carts.put(id, cartItem);
        return ResponseHandler.responseBuilder("success","Edit quantity item success", HttpStatus.OK,"",0);
    }

    @Override
    public void removeItem(int id) {
        carts.remove(id);
    }

    @Override
    public void clearItem() {
        carts.clear();
    }

    @Override
    public CartItem findCartItem(int id) {
        return carts.get(id);
    }

    @Override
    public List<CartItem> getAllItems() {
        return carts.values().isEmpty() ? new ArrayList<>() : new ArrayList<>(carts.values());
    }

    @Override
    public int getTotalMoney() {
        return carts.values().stream().mapToInt(item -> item.getNumProduct() * (item.getPrice() - (item.getPrice()* item.getDiscount()/100))).sum();
    }

    @Override
    public int getTotalQuantity() {
        return carts.values().stream().mapToInt(item -> item.getNumProduct()).sum();
    }

    @Override
    public boolean isCartEmpty() {
        if (this.carts.isEmpty())
            return true;
        return false;
    }
}
