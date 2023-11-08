package com.datn.laptopshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InforOrder {
    private Long userId;
    private String name;
    private String email;

    private String phone;
    private String address_delivery;

    private int num;
    private int totalMoney;
    private String payment;
    private String codeOrder;
}
