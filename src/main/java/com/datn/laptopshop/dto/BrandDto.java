package com.datn.laptopshop.dto;

import com.datn.laptopshop.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BrandDto {
    private int id;
    private String name;
    private String img;

    public BrandDto toBrandDto(Brand brand){
        return new BrandDto(brand.getId(), brand.getName(),brand.getImg());
    }

    public boolean isEmpty()  {
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.get(this)!=null) {
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Exception occured in processing");
            }
        }
        return true;
    }
}
