package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.dto.BrandDto;
import com.datn.laptopshop.dto.CategoryDto;
import com.datn.laptopshop.entity.Brand;
import com.datn.laptopshop.entity.Category;
import com.datn.laptopshop.repos.BrandRepository;
import com.datn.laptopshop.service.IBrandService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Page<BrandDto> findAll(int page, int limit, String search) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable p = PageRequest.of(page - 1,limit, sort);
        Page<Brand> pageBrand = brandRepository.findAll(search,p);

        if (pageBrand.isEmpty())
            return null;

        Page<BrandDto> pagebrandDto =pageBrand.map(n -> new BrandDto().toBrandDto(n));

        return pagebrandDto;
    }

    @Override
    public BrandDto findById(long id) {
        var bItem = brandRepository.findById(id);

        if (bItem.isPresent()){
            return new BrandDto().toBrandDto(bItem.get());
        }

        return null;
    }

    @Override
    public boolean insert(BrandDto brandDto) {
        // If the input is null, throw exception
        if (brandDto == null) {
            System.out.println("The input is null!"); return false;
        }

        // If the input is empty, throw exception
        if (brandDto.isEmpty()) {
            System.out.println("The input is empty!");return false;
        }

        // If the brand name already exists, throw exception
        if (brandRepository.existsByName(brandDto.getName())) {
            System.out.println("The brand name already exists!");return false;
        }

        Brand brandEntity = new Brand();
        brandEntity.setName(brandDto.getName());
        Brand brandSave = brandRepository.save(brandEntity);

        if (!brandRepository.existsById(brandSave.getId())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean update(BrandDto brandDto) {
        // If the input is null, throw exception
        if (brandDto == null) {
            System.out.println("The input is null!"); return false;
        }

        // If the input is empty, throw exception
        if (brandDto.isEmpty()) {
            System.out.println("The input is empty!");return false;
        }

        // If the data to be modified is not found, throw exception
        Optional<Brand> oldBrandEntity = brandRepository.findById(brandDto.getId());
        if (oldBrandEntity.isEmpty()) {
            System.out.println("The data to be modified is not found!");return false;
        }

        // If the new brand name is different from the old brand name and the new brand name already exists, throw exception
        if(!oldBrandEntity.get().getName().equals(brandDto.getName()) && brandRepository.existsByName(brandDto.getName())) {
            System.out.println("The brand name already exists!");return false;
        }

        oldBrandEntity.get().setName(brandDto.getName());
        Brand brandSave = brandRepository.save(oldBrandEntity.get());

        if (brandSave == null) {
            return false;
        }

        return true;
    }

    @Override
    public boolean delete(long id) {
        // If the data does not exist, throw exception
        if (!brandRepository.existsById(id)) {
            System.out.println("The data does not exist!");return false;
        }

        // Clear data based on input
        brandRepository.deleteById(id);

        // If the deleted data still exists, return false
        if (brandRepository.existsById(id)) {
            return false;
        }

        return true;
    }
}
