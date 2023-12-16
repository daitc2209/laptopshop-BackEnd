package com.datn.laptopshop.repos;

import com.datn.laptopshop.entity.Order;
import com.datn.laptopshop.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("SELECT o FROM OrderDetail o where o.order.id= ?1")
    List<OrderDetail> findByOrder(long id);

    @Query("SELECT od FROM Order o, OrderDetail od, Product p, Category c, Brand b " +
            "where o.id = od.order.id and od.product.id = p.id and c.id = p.category.id and b.id = p.brand.id " +
            "and o.stateCheckout = 1 and c.name = ?1")
    List<OrderDetail> getOrderRevenueByCategories(String name);

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

    @Query("select count(o) > 0 from OrderDetail o where o.product.id = ?1")
    boolean existsByProduct(long id);

}
