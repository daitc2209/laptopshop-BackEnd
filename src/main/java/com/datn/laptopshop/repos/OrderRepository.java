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

    @Query("SELECT o from Order o where concat(o.name,o.payment, o.codeOrder) like %?1% and" +
            " (?2 is null or o.stateOrder = ?2)")
    Page<Order> findAll(String search_text, StateOrder status, Pageable pageable);

    @Query("SELECT o from Order o where concat(o.name,o.payment, o.codeOrder) like %?1% " +
            " and (?2 is null or ?3 is null or o.created_at BETWEEN ?2 AND ?3)" +
            " and (?4 is null or o.stateOrder = ?4)")
    Page<Order> findOrderByRangeDayAdmin(String search_text, Date start, Date end, StateOrder status, Pageable pageable);

    @Query("SELECT o from Order o where (o.user.username = ?1 OR o.user.email = ?1)" +
            " and (?2 is null or o.stateOrder = ?2)")
    List<Order> findByOrderByStatus(String username, StateOrder status);

    @Query("SELECT o from Order o where (?1 is null or o.stateOrder = ?1)")
    List<Order> findByOrderByStatus(StateOrder status);

    @Query("SELECT o from Order o where (o.user.username = ?1 OR o.user.email = ?1)" +
            " and (?2 is null or ?3 is null or o.created_at BETWEEN ?2 AND ?3)" +
            " and (?4 is null or o.stateOrder = ?4)")
    List<Order> findOrderByRangeDay(String email, Date start, Date end, StateOrder status);
}
