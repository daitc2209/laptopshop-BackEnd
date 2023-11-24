package com.datn.laptopshop.repos;

import com.datn.laptopshop.entity.Order;
import com.datn.laptopshop.entity.OrderDetail;
import com.datn.laptopshop.enums.StateOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o from Order o where o.codeOrder = ?1")
    Optional<Order> findByCodeOrder(String codeOrder);

    @Query("SELECT o from Order o where o.user.email = ?1")
    List<Order> findByUser(String email);

    @Query("SELECT o from Order o where o.user.username = ?1")
    List<Order> findByUsername(String email);

    @Query("SELECT o from Order o where  (?1 is null or ?1 = '' or o.name like %?1%) " +
            "and (?2 is null or ?2 = '' or o.payment like %?2%) " +
            "and (?3 is null or o.stateOrder = ?3)")
    Page<Order> findAll(String name, String payment, StateOrder order, Pageable pageable);

}
