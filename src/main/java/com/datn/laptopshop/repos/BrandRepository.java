package com.datn.laptopshop.repos;

import com.datn.laptopshop.entity.Brand;
import com.datn.laptopshop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query("select b from Brand b where b.name = ?1")
    Optional<Brand> findByName(String name);

    @Query("Select b from Brand b where b.name like %?1%")
    Page<Brand> findAll(String keyword, Pageable pageable);

    @Query("select count(b) > 0 from Brand b where b.name = ?1")
    boolean existsByName(String name);
}
