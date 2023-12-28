package com.datn.laptopshop.dto;

import com.datn.laptopshop.entity.Favourite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FavoutiteDto {
    private int id;
    private ProductDto product;

    @Override
    public String toString() {
        return "FavoutiteDto{" +
                "id=" + id +
                ", product_id=" + product +
                '}';
    }
}
