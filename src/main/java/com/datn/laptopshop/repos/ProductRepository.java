package com.datn.laptopshop.repos;

import com.datn.laptopshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p where p.category.id = ?1")
    List<Product> findAllProductWithCategoryId(Long id);

//    @Query("SELECT p\n" +
//            "FROM Product p, Brand b, Category c \n" +
//            "WHERE p.brand.id = b.id AND p.category.id = c.id\n" +
//            "AND c.name LIKE %?1% AND b.name LIKE %?2% order by p.name asc")
//    List<Product> filterProduct(String brand, String category,int page, int limit);

    @Query("SELECT p\n" +
            "FROM Product p, Brand b, Category c \n" +
            "WHERE p.brand.id = b.id AND p.category.id = c.id\n" +
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
//    Page<Product> filterProduct(String brand,
//                                String category,
//                                Double minPrice,
//                                Double maxPrice,
//                                Pageable pageable
//    );

//    @Query("SELECT p\n" +
//            "FROM Product p, Brand b, Category c \n" +
//            "WHERE p.brand.id = b.id AND p.category.id = c.id\n" +
//            "AND (?1 IS NULL OR b.name LIKE %?1%)\n" +
//            "AND (?2 IS NULL OR c.name LIKE %?2%)\n" +
//            "AND (?3 IS NULL OR p.price >= ?3)\n" +
//            "AND (?4 IS NULL OR p.price <= ?4)\n" +
//            "ORDER BY CASE WHEN ?5 = 'asc' THEN p.name END ASC, " +
//            "         CASE WHEN ?5 = 'desc' THEN p.name END DESC, " +
//            "         CASE WHEN ?6 = 'asc' THEN p.price END ASC, " +
//            "         CASE WHEN ?6 = 'desc' THEN p.price END DESC"
//    )
//    List<Product> filterProductPost(String brand,
//                                String category,
//                                Double minPrice,
//                                Double maxPrice,
//                                String sortByName,
//                                String sortByPrice
//    );
}
