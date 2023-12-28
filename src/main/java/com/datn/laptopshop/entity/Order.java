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

    @Column(name = "code_order", length = 20)
    private String codeOrder;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "phone", length = 11, nullable = false)
    private String phone;

    @Column(name = "address_delivery", nullable = false)
    private String address_delivery;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date created_at;

    @Column(name = "amount", nullable = false)
    private int num;

    @Column(name = "total_money", nullable = false)
    private int total_money;

    @Column(name = "payment", length = 50, nullable = false)
    private String payment;

    @Column(name = "state_checkout")
    private StateCheckout stateCheckout;

    @Column(name = "state_order")
    private StateOrder stateOrder;

    @Column(name = "note")
    private String note;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderDetail> orderdetail;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CheckOut> checkouts;
}
