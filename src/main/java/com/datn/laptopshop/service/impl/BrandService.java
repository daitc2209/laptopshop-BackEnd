package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.dto.BrandDto;
import com.datn.laptopshop.entity.Brand;
import com.datn.laptopshop.repos.BrandRepository;
import com.datn.laptopshop.service.IBrandService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BrandService implements IBrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public List<BrandDto> findAll() {
        List<Brand> brandList = brandRepository.findAll();
        List<BrandDto> brandDtoList = new ArrayList<>();
        for (Brand b : brandList){
            BrandDto brandDto = new BrandDto().toBrandDto(b);
            brandDtoList.add(brandDto);
        }
        return brandDtoList;
    }
}
