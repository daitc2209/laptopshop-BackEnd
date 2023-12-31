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
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("select p from Product p where p.category.id = ?1 and p.stateProduct = 1")
    List<Product> findAllProductWithCategoryId(int id);

    @Query("select p from Product p where p.category.name = ?1 and p.stateProduct = 1")
    List<Product> findAllProductWithCategoryName(String name);

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
            "(?1 is null or ?1 = '' or concat(p.name,p.description) like %?1%) " +
            "and (?2 is null or ?2 = 0 or p.category.id = ?2) " +
            "and (?3 is null or ?3 = 0 or p.brand.id = ?3) ")
    Page<Product> findAll(String name,
                             int categoryId,
                             int brandId,
                             Pageable pageable);

    @Query("select count(p) > 0 from Product p where p.category.id = ?1")
    boolean existsByCategory(int id);

    @Query("select count(p) > 0 from Product p where p.brand.id = ?1")
    boolean existsByBrand(int id);
}
