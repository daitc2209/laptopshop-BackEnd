package com.datn.laptopshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RevenueProduct {

    private int id;
    private String name;
    private long amount;
    private String img;
    private String brandName;
    private String categoryName;
}
