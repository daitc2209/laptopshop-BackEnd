package com.datn.laptopshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FilterProductRequest {
    private String sort;
    private String cateogryName;
    private String brandName;
    private long minPrice;
    private long maxPrice;

    @Override
    public String toString() {
        return "FilterProductRequest{" +
                "sort='" + sort + '\'' +
                ", cateogryName='" + cateogryName + '\'' +
                ", brandName='" + brandName + '\'' +
                ", minPrice='" + minPrice + '\'' +
                ", maxPrice='" + maxPrice + '\'' +
                '}';
    }
}
