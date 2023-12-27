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

    @Query("SELECT YEAR(o.created_at) AS year, MONTH(o.created_at) as month, SUM(o.total_money) AS revenue " +
            "FROM Order o WHERE o.stateCheckout = 1" +
            " GROUP BY YEAR(o.created_at), MONTH(o.created_at)")
    List<Object[]> getOrderRevenueByMonth();

    @Query("SELECT YEAR(o.created_at) AS year, MONTH(o.created_at) as month, SUM(o.total_money) AS revenue " +
            "FROM Order o WHERE YEAR(o.created_at) = ?1 AND o.stateCheckout = 1" +
            "GROUP BY YEAR(o.created_at), MONTH(o.created_at)")
    List<Object[]> getOrderRevenueByYear(String year);

    @Query("SELECT p.id, p.name, SUM(od.num) AS amount, p.img, p.brand.name, p.category.name" +
            " FROM Order o, OrderDetail od, Product p " +
            "where o.id = od.order.id and od.product.id = p.id" +
            " and o.stateCheckout = 1 GROUP BY p.id, p.name, p.img ,p.brand, p.category")
    List<Object[]> getOrderRevenueByProduct();

    @Query("SELECT YEAR(o.created_at) AS year, MONTH(o.created_at) as month, DAY(o.created_at) as day, SUM(o.total_money) AS revenue " +
            "FROM Order o WHERE o.stateCheckout = 1 AND o.created_at BETWEEN ?1 AND ?2 " +
            "GROUP BY YEAR(o.created_at), MONTH(o.created_at), DAY(o.created_at)")
    List<Object[]> getOrderRevenueByRangeDay(Date start, Date end);

//    @Query("SELECT YEAR(o.created_at) AS year, MONTH(o.created_at) as month, count (o.id) AS amount " +
//            "FROM Order o" +
//            " GROUP BY YEAR(o.created_at), MONTH(o.created_at)")
//    List<Object[]> getOrderByMonth();
}
