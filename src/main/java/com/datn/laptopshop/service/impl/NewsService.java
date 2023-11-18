package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.dto.NewsDto;
import com.datn.laptopshop.entity.Category;
import com.datn.laptopshop.entity.New;
import com.datn.laptopshop.repos.CategoryRepository;
import com.datn.laptopshop.repos.NewsRepository;
import com.datn.laptopshop.service.INewsService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NewsService implements INewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Page<NewsDto> findAll(int page, int limit, String search) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable p = PageRequest.of(page - 1,limit, sort);
        Page<New> pageNews = newsRepository.findAll(search,p);

        if (pageNews.isEmpty())
            return null;

        Page<NewsDto> pageNewsDto =pageNews.map(n -> new NewsDto().toNewsDto(n));

        System.out.println("page data: "+ pageNewsDto.toString());

        return pageNewsDto;
    }

    @Override
    public Page<NewsDto> findAll(int page, int limit) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable p = PageRequest.of(page - 1,limit, sort);
        Page<New> pageNews = newsRepository.findAll(p);

        if (pageNews.isEmpty())
            return null;

        Page<NewsDto> pageNewsDto =pageNews.map(n -> new NewsDto().toNewsDto(n));

        System.out.println("page data: "+ pageNewsDto.toString());

        return pageNewsDto;
    }

    @Override
    public NewsDto findById(long id) {

        var newItem = newsRepository.findById(id);

        if (newItem.isPresent()){
            return new NewsDto().toNewsDto(newItem.get());
        }

        return null;
    }

    @Override
    public NewsDto insert(NewsDto newsDto) {
        if (newsDto == null)
            return null;

        New n = new New();
        var category = categoryRepository.findByName(newsDto.getCategoryName());
        if (!category.isPresent())
            return null;
        n.setCategory(category.get());
        n.setTitle(newsDto.getTitle());
        n.setImg(newsDto.getImg());
        n.setShortDescription(newsDto.getShortDescription());
        n.setContent(newsDto.getContent());
        n.setCreated_at(new Date());

        newsRepository.save(n);

        return new NewsDto().toNewsDto(n);
    }

    @Override
    public boolean update(NewsDto newsDto) {
        if(newsDto == null) {
            return false;
        }
        Optional<New> oldNewEntity = newsRepository.findById(newsDto.getId());
        if(!oldNewEntity.isPresent()) {
            return false;
        }

        New n = new New();
        n.setId(newsDto.getId());

        var category = categoryRepository.findByName(newsDto.getCategoryName());
        if (!category.isPresent())
            return false;
        n.setCategory(category.get());
        n.setTitle(newsDto.getTitle());
        n.setImg(newsDto.getImg());
        n.setShortDescription(newsDto.getShortDescription());
        n.setContent(newsDto.getContent());
        n.setUpdate_at(new Date());

        newsRepository.save(n);

        return true;
    }

    @Override
    public boolean delete(long id) {
        var d = newsRepository.findById(id);
        if (d.isPresent()){
            newsRepository.delete(d.get());
            return true;
        }
        return false;
    }

//    @Override
//    public List<NewsDto> search(String keyword) {
//        var k = newsRepository.search(keyword);
//
//        List<NewsDto> list = new ArrayList<>();
//        for (New n : k){
//            System.out.println("n: "+n.toString());
//            NewsDto newsDto = new NewsDto().toNewsDto(n);
//            var c = categoryRepository.findById(n.getId());
//            newsDto.setCategoryName(c.get().getName());
//            System.out.println("newsDto: "+newsDto.toString());
//            list.add(newsDto);
//        }
//        return list;
//    }
}
