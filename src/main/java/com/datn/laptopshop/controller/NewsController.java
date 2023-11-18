package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.NewsDto;
import com.datn.laptopshop.service.ICategoryService;
import com.datn.laptopshop.service.INewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NewsController {

    @Autowired
    private INewsService newsService;
    @Autowired
    private ICategoryService categoryService;
    @GetMapping("/news")
    public ResponseEntity<?> getNews(@RequestParam(name = "page", defaultValue = "1") int page){
        try {
            int limit = 2;
            Map m = new HashMap<>();
            var listNews = newsService.findAll(page,limit);
            m.put("listNews", listNews.getContent());
            m.put("totalPage", listNews.getTotalPages());
            m.put("currentPage", page);


            return ResponseHandler.responseBuilder("message","success",
                    HttpStatus.OK,m,0);
        }catch (Exception e){
            return ResponseHandler.responseBuilder("err","ERR NEWS",
                    HttpStatus.BAD_REQUEST,"",99);
        }

    }

    @GetMapping("/news/detail")
    public ResponseEntity<?> contactDetail(@RequestParam("id") long id, @RequestParam(name = "page", defaultValue = "1") int page) {

        try {
            Map m = new HashMap<>();
            m.put("NewsItem", newsService.findById(id));
            m.put("currentPage", page);

            return ResponseHandler.responseBuilder("message","success NEWS DETAIL",
                    HttpStatus.OK,m,0);
        }catch (Exception e){
            return ResponseHandler.responseBuilder("err","ERR NEWS DETAIL",
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }


}
