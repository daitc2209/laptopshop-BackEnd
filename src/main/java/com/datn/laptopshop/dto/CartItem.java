package com.datn.laptopshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private int productId;
    private String name;
    private int price;
    private int discount;
    private String img;
    private int quantity_in_stock;
    private int numProduct;
    private int totalPrice;

    @Override
    public String toString() {
        return "CartItem{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", img='" + img + '\'' +
                ", numProduct=" + numProduct +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
