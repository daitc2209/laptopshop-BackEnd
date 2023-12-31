package com.datn.laptopshop.dto;

import com.datn.laptopshop.entity.CheckOut;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutDto {
    private int id;
    private int order;
    private int user;
    private long amount;
    private String bankCode;
    private String cardType;
    private String orderInfo;
    private String payDate;

    public CheckOutDto toCheckOutDto(CheckOut checkOut){
        return new CheckOutDto(checkOut.getId(),checkOut.getOrder().getId(),checkOut.getUser().getId(),
                checkOut.getAmount(),checkOut.getBankCode(),checkOut.getCardType(),checkOut.getOrderInfo(),checkOut.getPayDate());
    }

}
