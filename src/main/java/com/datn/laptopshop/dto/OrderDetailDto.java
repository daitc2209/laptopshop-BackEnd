package com.datn.laptopshop.dto;

import com.datn.laptopshop.entity.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {
    private int id;
    private int order;
    private ProductDto product;
    private int price;
    private int discount;
    private int num;
    private int totalPrice;

    public OrderDetailDto(int id, int orderId, int price, int discount, int num, int totalPrice) {
        this.id=id;
        this.order=orderId;
        this.price=price;
        this.discount=discount;
        this.num=num;
        this.totalPrice=totalPrice;
    }

    public OrderDetailDto toOrderDetailDto(OrderDetail o){
        return new OrderDetailDto(o.getId(),o.getOrder().getId(),o.getPrice(),o.getDiscount(),o.getNum(),o.getTotalPrice());
    }
}
