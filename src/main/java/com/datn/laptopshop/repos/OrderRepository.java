package com.datn.laptopshop.repos;

import com.datn.laptopshop.entity.Order;
import com.datn.laptopshop.enums.StateCheckout;
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

    @Query("SELECT o from Order o where  (?1 is null or ?1 = '' or o.name like %?1%) " +
            "and (?2 is null or ?2 = '' or o.payment like %?2%) " +
            "and (?3 is null or o.stateOrder = ?3)")
    Page<Order> findAll(String name, String payment, StateOrder order, Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "WHERE (?1 is null or o.stateCheckout = ?1) " +
            "AND (?1 is null or o.stateOrder = ?2 )" +
            "AND (?3 is null or o.payment like %?3% )" +
            "AND (?4 is null and ?5 is null or o.created_at BETWEEN ?4 AND ?5)")
    Page<Order> revenue(StateCheckout stateCheckout, StateOrder stateOrder, String payment,
                        Date start, Date end, Pageable pageable);
}
