package com.datn.laptopshop.repos;

import com.datn.laptopshop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select c from Category c where c.name = ?1")
    Optional<Category> findByName(String name);

    @Query("Select c from Category c where c.name like %?1%")
    Page<Category> findAll(String keyword, Pageable pageable);

    @Query("select count(c) > 0 from Category c where c.name = ?1")
    boolean existsByName(String name);
}
