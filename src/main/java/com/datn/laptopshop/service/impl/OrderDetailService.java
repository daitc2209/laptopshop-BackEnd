package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.dto.OrderDetailDto;
import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.entity.Order;
import com.datn.laptopshop.entity.OrderDetail;
import com.datn.laptopshop.repos.OrderDetailRepository;
import com.datn.laptopshop.service.IOrderDetailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderDetailService implements IOrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<OrderDetailDto> findByOrder(int id) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(id);

        List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
        for (OrderDetail o : orderDetails){
            OrderDetailDto od = new OrderDetailDto().toOrderDetailDto(o);
            ProductDto p = new ProductDto().toProductDTO(o.getProduct());
            od.setProduct(p);
            orderDetailDtos.add(od);
        }

        return orderDetailDtos;
    }
}
