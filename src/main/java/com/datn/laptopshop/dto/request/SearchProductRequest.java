package com.datn.laptopshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchProductRequest {
    private String name;
    private int price;
    private int discount;
    private long categoryName;
    private long brandName;

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

    @Override
    public String toString() {
        return "SearchProductRequest{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", categoryName='" + categoryName + '\'' +
                ", brandName='" + brandName + '\'' +
                '}';
    }
}
