package com.datn.laptopshop.dto;

import com.datn.laptopshop.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private int id;
    private String name;

    public CategoryDto toCateDto(Category category){
        return new CategoryDto(category.getId(), category.getName());
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
