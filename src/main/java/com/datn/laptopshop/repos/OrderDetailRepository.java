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
}
