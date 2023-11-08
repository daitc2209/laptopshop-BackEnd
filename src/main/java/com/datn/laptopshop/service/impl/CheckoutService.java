package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.dto.CheckOutDto;
import com.datn.laptopshop.entity.CheckOut;
import com.datn.laptopshop.entity.Order;
import com.datn.laptopshop.entity.User;
import com.datn.laptopshop.repos.CheckOutRepository;
import com.datn.laptopshop.repos.OrderRepository;
import com.datn.laptopshop.repos.UserRepository;
import com.datn.laptopshop.service.ICheckoutService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class CheckoutService implements ICheckoutService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CheckOutRepository checkOutRepository;

    @Override
    public CheckOutDto insert(CheckOutDto checkoutDto) {
        if(checkoutDto == null) {
            return null;
        }

        CheckOut checkout = new CheckOut();

        Optional<Order> order = orderRepository.findById(checkoutDto.getOrder());
        if(order.isEmpty()) {
            return null;
        }
        checkout.setOrder(order.get());
        Optional<User> userEntity = userRepository.findById(checkoutDto.getUser());
        if(userEntity.isEmpty()) {
            return null;
        }
        checkout.setUser(userEntity.get());
        checkout.setAmount(checkoutDto.getAmount());
        checkout.setBankCode(checkoutDto.getBankCode());
        checkout.setCardType(checkoutDto.getCardType());
        checkout.setOrderInfo(checkoutDto.getOrderInfo());
        checkout.setPayDate(checkoutDto.getPayDate());

        CheckOut checkoutSave = checkOutRepository.save(checkout);
        if(!checkOutRepository.existsById(checkoutSave.getId())) {
            return null;
        }

        return new CheckOutDto().toCheckOutDto(checkoutSave);
    }
}
