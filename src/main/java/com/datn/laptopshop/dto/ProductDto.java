package com.datn.laptopshop.dto;

import com.datn.laptopshop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String categoryName;
    private String brandName;
    private String name;
    private Integer price;
    private Integer discount;
    private Integer quantity;
    private String img;
    private String description;

    public ProductDto toProductDTO(Product product) {
        return new ProductDto(product.getId(),
                product.getCategory().getName(),
                product.getBrand().getName(),
                product.getName(),
                product.getPrice(),
                product.getDiscount(),
                product.getQuantity(),
                product.getImg(),
                product.getDescription());
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
                '}';
    }
}
