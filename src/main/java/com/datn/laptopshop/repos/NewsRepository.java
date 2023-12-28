package com.datn.laptopshop.repos;

import com.datn.laptopshop.entity.New;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface NewsRepository extends JpaRepository<New, Integer> {

    @Query("Select k from New k where k.title like %?1%")
    Page<New> findAll(String keyword, Pageable pageable);
}
