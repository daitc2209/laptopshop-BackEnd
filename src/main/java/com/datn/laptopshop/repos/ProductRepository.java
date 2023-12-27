package com.datn.laptopshop.repos;

import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.entity.Product;
import com.datn.laptopshop.entity.User;
import com.datn.laptopshop.enums.AuthenticationType;
import com.datn.laptopshop.enums.StateUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p where p.category.id = ?1 and p.stateProduct = 1")
    List<Product> findAllProductWithCategoryId(Long id);

    @Query("SELECT p\n" +
            "FROM Product p, Brand b, Category c \n" +
            "WHERE p.brand.id = b.id AND p.category.id = c.id and p.stateProduct = 1 \n" +
            "AND (?1 IS NULL OR ?1 = '' OR b.name LIKE %?1%)\n" +
            "AND (?2 IS NULL OR ?2 = '' OR c.name LIKE %?2%)\n" +
            "AND (?3 IS NULL OR p.price >= ?3)\n" +
            "AND (?4 IS NULL OR p.price <= ?4)\n"
            )
    Page<Product> filterProduct(String brand,
                                String category,
                                Double minPrice,
                                Double maxPrice,
                                Pageable pageable
                                );
    @Query("SELECT p FROM Product p where p.stateProduct = 1 and concat(p.name, p.brand.name, p.category.name) like %?1% ")
    List<Product> findByNameStartsWith(String term);

    @Query("select p from Product p where " +
            "(?1 is null or ?1 = '' or p.name like %?1%) " +
            "and (?2 is null or ?2 = -1 or p.price = ?2) " +
            "and (?3 is null or ?3 = -1 or p.discount = ?3) " +
            "and (?4 is null or ?4 = 0 or p.category.id = ?4) " +
            "and (?5 is null or ?5 = 0 or p.brand.id = ?5) ")
    Page<Product> findAll(String name,
                             int price,
                             int discount,
                             long categoryName,
                             long brandName,
                             Pageable pageable);

    @Query("select count(p) > 0 from Product p where p.category.id = ?1")
    boolean existsByCategory(long id);

    @Query("select count(p) > 0 from Product p where p.brand.id = ?1")
    boolean existsByBrand(long id);
}
