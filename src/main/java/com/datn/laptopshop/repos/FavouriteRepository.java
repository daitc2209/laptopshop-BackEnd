package com.datn.laptopshop.repos;

import com.datn.laptopshop.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    @Query("select f from Favourite f where f.user.id = ?1")
    List<Favourite> findFavourByUser(long id);

    @Query("select count(f) > 0 from Favourite f where f.user.id = ?1 and f.product.id = ?2")
    boolean existsProductInFavour(long user_id, long product_id);
}
