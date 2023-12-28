package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.FavoutiteDto;

import java.util.List;

public interface IFavouriteService {
    List<FavoutiteDto> findAll(String username);
    boolean insert(String username, int product_id);
    boolean delete(int id);
}
