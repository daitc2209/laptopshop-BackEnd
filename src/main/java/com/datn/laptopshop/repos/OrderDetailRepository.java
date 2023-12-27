package com.datn.laptopshop.repos;

import com.datn.laptopshop.entity.Order;
import com.datn.laptopshop.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("SELECT o FROM OrderDetail o where o.order.id= ?1")
    List<OrderDetail> findByOrder(long id);

    @Query("SELECT od FROM Order o, OrderDetail od, Product p, Category c, Brand b " +
            "where o.id = od.order.id and od.product.id = p.id and c.id = p.category.id and b.id = p.brand.id " +
            "and o.stateCheckout = 1 and c.name = ?1")
    List<OrderDetail> getOrderRevenueByCategories(String name);

    @Query("select count(o) > 0 from OrderDetail o where o.product.id = ?1")
    boolean existsByProduct(long id);

}
