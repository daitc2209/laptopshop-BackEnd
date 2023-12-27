package com.datn.laptopshop.dto;

import com.datn.laptopshop.entity.Product;
import com.datn.laptopshop.enums.StateProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String categoryName;
    private String brandName;
    private String name;
    private int price;
    private int discount;
    private int quantity;
    private String img;
    private String description;
    private StateProduct state;

    public ProductDto toProductDTO(Product product) {
        return new ProductDto(product.getId(),
                product.getCategory().getName(),
                product.getBrand().getName(),
                product.getName(),
                product.getPrice(),
                product.getDiscount(),
                product.getQuantity(),
                product.getImg(),
                product.getDescription(),
                product.getStateProduct());
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

    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", brandName='" + brandName + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", quantity=" + quantity +
                ", img='" + img + '\'' +
                ", description='" + description + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
