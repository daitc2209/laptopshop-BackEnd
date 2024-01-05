package com.datn.laptopshop.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.datn.laptopshop.dto.BrandDto;
import com.datn.laptopshop.dto.CategoryDto;
import com.datn.laptopshop.entity.Brand;
import com.datn.laptopshop.entity.Category;
import com.datn.laptopshop.repos.BrandRepository;
import com.datn.laptopshop.repos.ProductRepository;
import com.datn.laptopshop.service.IBrandService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class BrandService implements IBrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Cloudinary cloudinary;

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
    public BrandDto findById(int id) {
        var bItem = brandRepository.findById(id);

        if (bItem.isPresent()){
            return new BrandDto().toBrandDto(bItem.get());
        }

        return null;
    }

    @Override
    public boolean insert(String name, MultipartFile img) {
        // If the input is null, throw exception
        if (name == null || name =="") {
            System.out.println("The input is null!"); return false;
        }

        // If the brand name already exists, throw exception
        if (brandRepository.existsByName(name)) {
            System.out.println("The brand name already exists!");return false;
        }

        BrandDto brandDto = new BrandDto();
        brandDto.setName(name);
        try {
            Map r = cloudinary.uploader()
                        .upload(img.getBytes(), ObjectUtils.asMap("folder","images/banner"));
            brandDto.setImg((String) r.get("url"));
        }catch (Exception e)
        {
            e.printStackTrace();
            brandDto.setImg(null);
        }

        Brand brandEntity = new Brand();
        brandEntity.setName(brandDto.getName());
        brandEntity.setImg(brandDto.getImg());
        Brand brandSave = brandRepository.save(brandEntity);

        if (!brandRepository.existsById(brandSave.getId())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean update(int id, String name, MultipartFile img) {
        // If the input is null, throw exception
        if (name == null) {
            System.out.println("The input is null!"); return false;
        }

        // If the data to be modified is not found, throw exception
        Optional<Brand> oldBrandEntity = brandRepository.findById(id);
        if (oldBrandEntity.isEmpty()) {
            System.out.println("The data to be modified is not found!");return false;
        }

        // If the new brand name is different from the old brand name and the new brand name already exists, throw exception
        if(!oldBrandEntity.get().getName().equals(name) && brandRepository.existsByName(name)) {
            System.out.println("The brand name already exists!");return false;
        }

        if (name != "" && name != null)
            oldBrandEntity.get().setName(name);
        if (img != null) {
            try {
                Map r = cloudinary.uploader()
                        .upload(img.getBytes(), ObjectUtils.asMap("folder", "images/banner"));
                oldBrandEntity.get().setImg((String) r.get("url"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Brand brandSave = brandRepository.save(oldBrandEntity.get());

        if (brandSave == null) {
            return false;
        }

        return true;
    }

    @Override
    public boolean delete(int id) {
        // If the data does not exist, throw exception
        if (!brandRepository.existsById(id)) {
            System.out.println("The brand does not exist!");return false;
        }

        if (productRepository.existsByBrand(id)) {
            System.out.println("The brand exist in Product");
            return false;
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
