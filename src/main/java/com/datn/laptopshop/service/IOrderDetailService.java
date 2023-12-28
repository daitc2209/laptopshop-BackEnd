package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.OrderDetailDto;

import java.util.List;

public interface IOrderDetailService {
    List<OrderDetailDto> findByOrder(int id);
}
