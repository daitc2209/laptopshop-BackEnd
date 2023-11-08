package com.datn.laptopshop.repos;

import com.datn.laptopshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o from Order o where o.codeOrder = ?1")
    Optional<Order> findByCodeOrder(String codeOrder);

    @Query("SELECT o from Order o where o.user.email = ?1")
    List<Order> findByUser(String email);
}
