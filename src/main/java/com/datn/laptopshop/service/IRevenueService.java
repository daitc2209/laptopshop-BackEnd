package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.Revenue;
import com.datn.laptopshop.dto.RevenueCategories;
import com.datn.laptopshop.dto.RevenueProduct;

import java.util.List;

public interface IRevenueService {
    //    List<Revenue> revenue(int page, int limit, StateCheckout stateCheckout, StateOrder stateOrder, String payment, Date start, Date end);
    List<RevenueCategories> revenueWithCategories();

    List<Revenue> revenue();

    List<Revenue> revenueYear(String year);

    List<RevenueProduct> revenueProduct();
}
