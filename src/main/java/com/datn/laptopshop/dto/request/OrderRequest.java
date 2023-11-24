package com.datn.laptopshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private String typePayment;
    private String phone;

    private String address;
    private String note;
    private String bankCode;

    @Override
    public String toString() {
        return "OrderRequest{" +
                "typePayment='" + typePayment + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", bankCode='" + bankCode + '\'' +
                '}';
    }
}
