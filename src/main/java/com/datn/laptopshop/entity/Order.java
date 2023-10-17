package com.datn.laptopshop.entity;

import com.datn.laptopshop.enums.StateCheckout;
import com.datn.laptopshop.enums.StateOrder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id")
    private User user;

    @Column(name = "code_order")
    private String codeOrder;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address_delivery")
    private String address_delivery;

    @Column(name = "created_at")
    private Date created_at;

    @Column(name = "num")
    private int num;

    @Column(name = "total_money")
    private int total_money;

    @Column(name = "payment")
    private String payment;

    @Column(name = "state_checkout")
    private StateCheckout stateCheckout;

    @Column(name = "state_order")
    private StateOrder stateOrder;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderDetail> orderdetail;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CheckOut> checkouts;
}
