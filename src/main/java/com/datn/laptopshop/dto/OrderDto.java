package com.datn.laptopshop.dto;

import com.datn.laptopshop.entity.Order;
import com.datn.laptopshop.enums.StateCheckout;
import com.datn.laptopshop.enums.StateOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private Long user;
    private String codeOrder;
    private String name;
    private String email;
    private String phone;
    private String address_delivery;
    private Date created_at;
    private int num;
    private int total_money;
    private String payment;
    private StateCheckout stateCheckout;
    private StateOrder stateOrder;
    private List<OrderDetailDto> orderdetail;

    public OrderDto(Long id, Long idUser, String codeOrder, String name, String email,
                    String phone, String addressDelivery, Date createdAt,
                    int num, int totalMoney, String payment, StateCheckout stateCheckout, StateOrder stateOrder) {
        this.id = id;
        this.user=idUser;
        this.codeOrder = codeOrder;
        this.name=name;
        this.email=email;
        this.phone=phone;
        this.address_delivery=addressDelivery;
        this.created_at=createdAt;
        this.num=num;
        this.total_money=totalMoney;
        this.payment=payment;
        this.stateCheckout=stateCheckout;
        this.stateOrder=stateOrder;
    }

    public OrderDto toOrderDto(Order o){
        return new OrderDto(o.getId(),o.getUser().getId(),o.getCodeOrder(),o.getName(),
                o.getEmail(),o.getPhone(),o.getAddress_delivery(),o.getCreated_at(),o.getNum(),
                o.getTotal_money(),o.getPayment(),o.getStateCheckout(),o.getStateOrder());
    }

    @Override
    public String toString() {
        return "OrderDto{" +
                "id=" + id +
                ", user=" + user +
                ", codeOrder='" + codeOrder + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address_delivery='" + address_delivery + '\'' +
                ", created_at=" + created_at +
                ", num=" + num +
                ", total_money=" + total_money +
                ", payment='" + payment + '\'' +
                ", stateCheckout=" + stateCheckout +
                ", stateOrder=" + stateOrder +
                ", orderdetail=" + orderdetail +
                '}';
    }
}
