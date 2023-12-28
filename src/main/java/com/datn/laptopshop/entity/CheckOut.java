package com.datn.laptopshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "checkouts")
public class CheckOut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checkout_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "id")
    private User user;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "bank_code", nullable = false)
    private String bankCode;

    @Column(name = "card_type", nullable = false)
    private String cardType;

    @Column(name = "order_info", nullable = false)
    private String orderInfo;

    @Column(name = "pay_date", nullable = false)
    private String payDate;

}
