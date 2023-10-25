package com.datn.laptopshop.dto;

import com.datn.laptopshop.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;

    public CategoryDto toCateDto(Category category){
        return new CategoryDto(category.getId(), category.getName());
    }
}
