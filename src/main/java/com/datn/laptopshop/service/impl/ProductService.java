package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.OrderDetailDto;
import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.dto.UserDto;
import com.datn.laptopshop.dto.request.FilterProductRequest;
import com.datn.laptopshop.dto.request.SearchProductRequest;
import com.datn.laptopshop.entity.Brand;
import com.datn.laptopshop.entity.Category;
import com.datn.laptopshop.entity.Product;
import com.datn.laptopshop.entity.User;
import com.datn.laptopshop.enums.StateProduct;
import com.datn.laptopshop.enums.StateUser;
import com.datn.laptopshop.repos.BrandRepository;
import com.datn.laptopshop.repos.CategoryRepository;
import com.datn.laptopshop.repos.OrderDetailRepository;
import com.datn.laptopshop.repos.ProductRepository;
import com.datn.laptopshop.service.IProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class ProductService implements IProductService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    OrderDetailRepository OrderDetailRepository;

    @Override
    public List<ProductDto> findByCategoryId(long id) {

        List<Product> listProduct = productRepository.findAllProductWithCategoryId(id);
        if(listProduct == null)
            return null;
        List<ProductDto> listProductDto = new ArrayList<>();
        for (Product product : listProduct) {
            System.out.println("product: "+ product);
            ProductDto dto = new ProductDto().toProductDTO(product);
            System.out.println("dto: "+ dto.toString());
            listProductDto.add(dto);
        }

        return listProductDto;
    }

    @Override
    public Page<ProductDto> findAll(FilterProductRequest filterProduct, int page, int limit) {
        double minPrice = 0;
        double maxPrice = 30000000;
        Sort sort = Sort.by(Sort.Direction.ASC, "price");
        if (filterProduct.getBrandName().equals("all"))
            filterProduct.setBrandName("");
        if (filterProduct.getCateogryName().equals("all"))
            filterProduct.setCateogryName("");
        if (filterProduct.getSort().equals("high-low"))
            sort = Sort.by(Sort.Direction.DESC, "price");
        if (filterProduct.getSort().equals("a-z"))
            sort = Sort.by(Sort.Direction.ASC, "name");
        if (filterProduct.getSort().equals("z-a"))
            sort = Sort.by(Sort.Direction.DESC, "name");
        minPrice = filterProduct.getMinPrice();
        maxPrice = filterProduct.getMaxPrice();

        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Page<Product> product = productRepository.filterProduct(
                filterProduct.getBrandName(), filterProduct.getCateogryName(), minPrice, maxPrice, pageable);

        if(product == null)
            return null;
        List<ProductDto> listProductDto = new ArrayList<>();
        for (Product p : product.getContent()) {
            ProductDto dto = new ProductDto().toProductDTO(p);
            listProductDto.add(dto);
        }

        return new PageImpl<>(listProductDto, pageable, product.getTotalElements());
    }

    @Override
    public ProductDto findProductId(long id) {
        try{
            var product =  productRepository.findById(id);
            if (product.isPresent()){
                ProductDto dto = new ProductDto().toProductDTO(product.get());
                System.out.println("dto trong product id: "+dto);
                return dto;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProductDto> findByNameSearch(String term) {
        List<Product> list = productRepository.findByNameStartsWith(term);
        if (list.isEmpty())
            return null;
        List<ProductDto> list1 = new ArrayList<>();
        for (Product p : list)
            list1.add(new ProductDto().toProductDTO(p));

        return list1;
    }

    @Override
    public Page<ProductDto> findAll(int page, int limit, SearchProductRequest search) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable p = PageRequest.of(page - 1, limit, sort);
        System.out.println("vao duoc service");

        Page<Product> pageUser = productRepository.findAll(search.getName(),search.getPrice(),
                search.getDiscount(),search.getCategoryName(),search.getBrandName(),p);

        if (pageUser.isEmpty())
            return null;

        Page<ProductDto> pageUserDto = pageUser.map(product -> new ProductDto().toProductDTO(product));

        return pageUserDto;
    }

    @Override
    public boolean insert(ProductDto productDto) {
    // If the input is null, throw exception
        if (productDto == null) {
            return false;
        }

        // If the input is empty, throw exception
        if (productDto.isEmpty()) {
            return false;
        }

        // If insert data failed, return null
        Product productEntity = new Product();

        productEntity.setId(productDto.getId());
        Optional<Category> categoryEntity = categoryRepository.findByName(productDto.getCategoryName());
        productEntity.setCategory(categoryEntity.get());
        Optional<Brand> brandEntity = brandRepository.findByName(productDto.getBrandName());
        productEntity.setBrand(brandEntity.get());
        productEntity.setName(productDto.getName());
        productEntity.setPrice(productDto.getPrice());
        productEntity.setDiscount(productDto.getDiscount());
        productEntity.setQuantity(productDto.getQuantity());
        productEntity.setImg(productDto.getImg());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setStateProduct(productDto.getState());

        Product productSave = productRepository.save(productEntity);
        if (!productRepository.existsById(productSave.getId())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean update(ProductDto productDto) {
// If the input is null, throw exception
        if (productDto == null) {
            System.out.println("product null");
            return false;
        }

        // If the input is empty, throw exception
        if (productDto.isEmpty()) {
            System.out.println("product Empty");
            return false;
        }

        // If the data to be modified is not found, throw exception
        Optional<Product> oldProductEntity = productRepository.findById(productDto.getId());
        if (oldProductEntity.isEmpty()) {
            System.out.println("product not found");
            return false;
        }

        // If the new product name is different from the old product name and the new
        // product name already exists, throw exception

        // If saving modification fail, return false
        Category categoryEntity = categoryRepository.findByName(productDto.getCategoryName()).get();
        oldProductEntity.get().setCategory(categoryEntity);
        Brand brandEntity = brandRepository.findByName(productDto.getBrandName()).get();
        oldProductEntity.get().setBrand(brandEntity);
        oldProductEntity.get().setName(productDto.getName());
        oldProductEntity.get().setPrice(productDto.getPrice());
        oldProductEntity.get().setDiscount(productDto.getDiscount());
        if (productDto.getImg() != null)
            oldProductEntity.get().setImg(productDto.getImg());
        oldProductEntity.get().setQuantity(productDto.getQuantity());
        oldProductEntity.get().setDescription(productDto.getDescription());
        oldProductEntity.get().setStateProduct(productDto.getState());

        Product productSave = productRepository.save(oldProductEntity.get());
        if (productSave == null) {
            return false;
        }

        return true;
    }

    @Override
    public boolean delete(long id) {
        // If the data does not exist, throw exception
        if (!productRepository.existsById(id)) {
            System.out.println("product not exist!!");
            return false;
        }
        Optional<Product> p = productRepository.findById(id);
        if (OrderDetailRepository.existsByProduct(id)){
            System.out.println("product exist in OrderDetail !!!!");
            return false;
        }

        // Clear data based on input
        productRepository.deleteById(id);

        // If the deleted data still exists, return false
        if (productRepository.existsById(id)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean updateQuantityProduct(List<OrderDetailDto> list) {

        for (OrderDetailDto order : list)
        {
            var p = productRepository.findById(order.getProduct().getId());
            if (p.isEmpty())
                return false;
            var oldQuantity = p.get().getQuantity();
            if (oldQuantity < order.getNum())
                return false;
            p.get().setQuantity(oldQuantity - order.getNum());
            productRepository.save(p.get());
        }

        return true;
    }

    @Override
    public boolean stateProduct(long id, int state) {
        var product = productRepository.findById(id);
        if (product.isEmpty())
            return false;

        // If saving modification fail, return false
        if (state == 1)
            product.get().setStateProduct(StateProduct.DISABLED);
        if (state == 0)
            product.get().setStateProduct(StateProduct.ACTIVED);

        productRepository.save(product.get());

        return true;
    }


}
