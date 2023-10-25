package com.datn.laptopshop.dto;

import com.datn.laptopshop.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BrandDto {
    private Long id;
    private String name;

    public BrandDto toBrandDto(Brand brand){
        return new BrandDto(brand.getId(), brand.getName());
    }
}
