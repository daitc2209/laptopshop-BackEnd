package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.NewsDto;
import org.springframework.data.domain.Page;


public interface INewsService {

    Page<NewsDto> findAll(int page, int limit, String search);

    Page<NewsDto> findAll(int page, int limit);

    NewsDto findById(long id);

    NewsDto insert(NewsDto newsDto);

    boolean update(NewsDto newsDto);

    boolean delete(long id);

//    List<NewsDto> search(String keyword);
}
