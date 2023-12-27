package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.Revenue;
import com.datn.laptopshop.dto.RevenueCategories;
import com.datn.laptopshop.dto.RevenueProduct;

import java.util.Date;
import java.util.List;

public interface IRevenueService {
    List<RevenueCategories> revenueWithCategories();

    List<Revenue> revenue();

    List<Revenue> revenueYear(String year);

    List<RevenueProduct> revenueProduct();

    List<Revenue> revenueRangeDay(Date start, Date end);

//    List<Revenue> statisticalByOrderWithMonth();
}
