package com.datn.laptopshop.entity;

import com.datn.laptopshop.enums.StateProduct;
import com.datn.laptopshop.enums.StateUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "name", length = 300, unique = true)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "discount")
    private int discount;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "img", length = 500)
    private String img;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "state")
    private StateProduct stateProduct;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Favourite> favourites;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderDetail> order_details;

}
