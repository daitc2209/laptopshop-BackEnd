package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.dto.CategoryDto;
import com.datn.laptopshop.dto.NewsDto;
import com.datn.laptopshop.entity.Category;
import com.datn.laptopshop.entity.New;
import com.datn.laptopshop.repos.CategoryRepository;
import com.datn.laptopshop.service.ICategoryService;
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
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> findAll() {
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty()){
            return null;
        }
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : categories){
            CategoryDto categoryDto = new CategoryDto().toCateDto(category);
            categoryDtos.add(categoryDto);
        }
        return categoryDtos;
    }

    @Override
    public Page<CategoryDto> findAll(int page, int limit, String search) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable p = PageRequest.of(page - 1,limit, sort);
        Page<Category> pageCate = categoryRepository.findAll(search,p);

        if (pageCate.isEmpty())
            return null;

        Page<CategoryDto> pageDto =pageCate.map(n -> new CategoryDto().toCateDto(n));

        return pageDto;
    }

    @Override
    public CategoryDto findById(long id) {
        var cItem = categoryRepository.findById(id);

        if (cItem.isPresent()){
            return new CategoryDto().toCateDto(cItem.get());
        }

        return null;
    }

    @Override
    public boolean insert(CategoryDto categoryDto) {
        // If the input is null, throw exception
        if (categoryDto == null) {
            System.out.println("categoryDto null");
            return false;
        }

        // If the input is empty, throw exception
        if (categoryDto.isEmpty()) {
            System.out.println("The input is empty!");return false;
        }

        // If the brand name already exists, throw exception
        if (categoryRepository.existsByName(categoryDto.getName())) {
            System.out.println("The category name already exists!");return false;
        }

        // If insert data failed, return null
        Category categoryEntity = new Category();
        categoryEntity.setName(categoryDto.getName());
        Category categorySave = categoryRepository.save(categoryEntity);

        if (!categoryRepository.existsById(categorySave.getId())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean update(CategoryDto categoryDto) {
        // If the input is null, throw exception
        if (categoryDto == null) {
            System.out.println("The input is null!");return false;
        }

        // If the input is empty, throw exception
        if (categoryDto.isEmpty()) {
            System.out.println("The input is empty!");return false;
        }

        // If the data to be modified is not found, throw exception
        Optional<Category> oldCategoryEntity = categoryRepository.findById(categoryDto.getId());
        if (oldCategoryEntity.isEmpty()) {
            System.out.println("The data to be modified is not found!");return false;
        }

        // If the new brand name is different from the old brand name and the new brand
        // name already exists, throw exception
        if (!oldCategoryEntity.get().getName().equals(categoryDto.getName()) && categoryRepository.existsByName(categoryDto.getName())) {
            System.out.println("The category name already exists!");return false;
        }

        // If saving modification fail, return false
        oldCategoryEntity.get().setName(categoryDto.getName());
        Category categorySave = categoryRepository.save(oldCategoryEntity.get());

        if (categorySave == null) {
            return false;
        }

        return true;
    }

    @Override
    public boolean delete(long id) {
        // If the data does not exist, throw exception
        if (!categoryRepository.existsById(id)) {
            System.out.println("The category does not exist!");
            return false;
        }

        // Clear data based on input
        categoryRepository.deleteById(id);

        // If the deleted data still exists, return false
        if (categoryRepository.existsById(id)) {
            return false;
        }

        return true;
    }
}
