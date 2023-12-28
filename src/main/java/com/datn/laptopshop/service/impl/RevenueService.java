package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.dto.Revenue;
import com.datn.laptopshop.dto.RevenueCategories;
import com.datn.laptopshop.dto.RevenueProduct;
import com.datn.laptopshop.entity.Category;
import com.datn.laptopshop.entity.OrderDetail;
import com.datn.laptopshop.repos.CategoryRepository;
import com.datn.laptopshop.repos.OrderDetailRepository;
import com.datn.laptopshop.repos.OrderRepository;
import com.datn.laptopshop.service.IRevenueService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class RevenueService implements IRevenueService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<RevenueCategories> revenueWithCategories() {

        var category = categoryRepository.findAll();

        if (category.isEmpty())
            return null;
        List<RevenueCategories> revenueCategoriesList = new ArrayList<>();
        for (Category c : category)
        {
            int totalMoney=0;
            int totalSell=0;
            String name="";
            var listRevenue = orderDetailRepository.getOrderRevenueByCategories(c.getName());
            if (!listRevenue.isEmpty()){
                for (OrderDetail revenue : listRevenue)
                {
                    name = revenue.getProduct().getName();
                    totalSell += revenue.getNum();
                    totalMoney += revenue.getTotalPrice();
                    System.out.println("name: "+name + "  !!! num: "+revenue.getNum());
                }
            }
            RevenueCategories r = new RevenueCategories();
            r.setName(c.getName());
            r.setTotal_sell(totalSell);
            r.setTotal_money(totalMoney);
            revenueCategoriesList.add(r);
        }
        return revenueCategoriesList;
    }

    @Override
    public List<Revenue> revenue() {

        List<Object[]> list = orderRepository.getOrderRevenueByMonth();
        List<Revenue> revenueList = new ArrayList<>();
        for (Object[] obj : list ){
            Revenue r = new Revenue();
            r.setYear((int) obj[0]);
            r.setMonth((int) obj[1]);
            r.setTotal_money_month((long) obj[2]);
            revenueList.add(r);
        }

        return revenueList;
    }

    @Override
    public List<Revenue> revenueYear(String year) {
        List<Object[]> list = orderRepository.getOrderRevenueByYear(year);
        List<Revenue> revenueList = new ArrayList<>();
        for (Object[] obj : list ){
            Revenue r = new Revenue();
            r.setYear((int) obj[0]);
            r.setMonth((int) obj[1]);
            r.setTotal_money_month((long) obj[2]);
            revenueList.add(r);
        }

        return revenueList;
    }

    @Override
    public List<RevenueProduct> revenueProduct() {
        List<Object[]> list = orderRepository.getOrderRevenueByProduct();
        List<RevenueProduct> revenueProductList = new ArrayList<>();
        for (Object[] obj : list){
            RevenueProduct r = new RevenueProduct();
            r.setId((int) obj[0]);
            r.setName((String) obj[1]);
            r.setAmount((long) obj[2]);
            r.setImg((String) obj[3]);
            r.setBrandName((String) obj[4]);
            r.setCategoryName((String) obj[5]);
            revenueProductList.add(r);
        }
        return revenueProductList;
    }

    @Override
    public List<Revenue> revenueRangeDay(Date start, Date end) {
        List<Object[]> list = orderRepository.getOrderRevenueByRangeDay(start, end);

        List<Revenue> revenueList = new ArrayList<>();
        for (Object[] obj : list ){
            Revenue r = new Revenue();
            r.setYear((int) obj[0]);
            r.setMonth((int) obj[1]);
            r.setDay((int) obj[2]);
            r.setTotal_money_day((long) obj[3]);
            revenueList.add(r);
        }

        return revenueList;
    }

//    @Override
//    public List<Revenue> statisticalByOrderWithMonth() {
//        List<Object[]> list = orderRepository.getOrderByMonth();
//        List<Revenue> revenueList = new ArrayList<>();
//        for (Object[] obj : list ){
//            Revenue r = new Revenue();
//            r.setYear((int) obj[0]);
//            r.setMonth((int) obj[1]);
//            r.setTotal_money_month((long) obj[2]);
//            revenueList.add(r);
//        }
//
//        return revenueList;
//    }


}
