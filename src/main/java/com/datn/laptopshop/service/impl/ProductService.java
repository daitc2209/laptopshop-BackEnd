package com.datn.laptopshop.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public List<ProductDto> findByCategoryId(int id) {

        List<Product> listProduct = productRepository.findAllProductWithCategoryId(id);
        if(listProduct == null)
            return null;
        List<ProductDto> listProductDto = new ArrayList<>();
        for (Product product : listProduct) {
            ProductDto dto = new ProductDto().toProductDTO(product);
            listProductDto.add(dto);
        }

        return listProductDto;
    }

    @Override
    public List<ProductDto> findByCategoryName(String name) {
        List<Product> listProduct = productRepository.findAllProductWithCategoryName(name);
        if(listProduct == null)
            return null;
        List<ProductDto> listProductDto = new ArrayList<>();
        for (Product product : listProduct) {
            ProductDto dto = new ProductDto().toProductDTO(product);
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
    public ProductDto findProductId(int id) {
        try{
            var product =  productRepository.findById(id);
            if (product.isPresent()){
                ProductDto dto = new ProductDto().toProductDTO(product.get());
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

        Page<Product> pageUser = productRepository.findAll(search.getText(),search.getCategoryId(),search.getBrandId(),p);

        if (pageUser.isEmpty())
            return null;

        Page<ProductDto> pageUserDto = pageUser.map(product -> new ProductDto().toProductDTO(product));

        return pageUserDto;
    }

    @Override
    public boolean insert(ProductDto productDto, MultipartFile fileImage) {
        if (productDto.isEmpty()) {
            return false;
        }

        try{
            Map r = cloudinary.uploader().upload(fileImage.getBytes(), ObjectUtils.asMap("folder","images/product"));
            String nameImage = (String) r.get("url");
            productDto.setImg(nameImage);
        }catch (Exception e){
            productDto.setImg(null);
        }

        // If insert data failed, return null
        Product product = new Product();

        product.setId(productDto.getId());
        Optional<Category> categoryEntity = categoryRepository.findByName(productDto.getCategoryName());
        product.setCategory(categoryEntity.get());
        Optional<Brand> brandEntity = brandRepository.findByName(productDto.getBrandName());
        product.setBrand(brandEntity.get());
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setDiscount(productDto.getDiscount());
        product.setQuantity(productDto.getQuantity());
        product.setImg(productDto.getImg());
        product.setDescription(productDto.getDescription());
        product.setStateProduct(productDto.getState());

        Product productSave = productRepository.save(product);
        if (!productRepository.existsById(productSave.getId())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean update(ProductDto productDto, MultipartFile fileImage) {
        if (productDto.isEmpty()) {
            System.out.println("product Empty");
            return false;
        }

        Optional<Product> oldProduct = productRepository.findById(productDto.getId());
        if (oldProduct.isEmpty()) {
            System.out.println("product not found");
            return false;
        }


        try{
            Map r = cloudinary.uploader().upload(fileImage.getBytes(), ObjectUtils.asMap("folder","images/product"));
            String nameImage = (String) r.get("url");
            productDto.setImg(nameImage);
        }catch (Exception e){
            productDto.setImg(null);
        }

        Category category = categoryRepository.findByName(productDto.getCategoryName()).get();
        oldProduct.get().setCategory(category);
        Brand brandEntity = brandRepository.findByName(productDto.getBrandName()).get();
        oldProduct.get().setBrand(brandEntity);
        oldProduct.get().setName(productDto.getName());
        oldProduct.get().setPrice(productDto.getPrice());
        oldProduct.get().setDiscount(productDto.getDiscount());
        if (productDto.getImg() != null)
            oldProduct.get().setImg(productDto.getImg());
        oldProduct.get().setQuantity(productDto.getQuantity());
        oldProduct.get().setDescription(productDto.getDescription());
        oldProduct.get().setStateProduct(productDto.getState());

        Product productSave = productRepository.save(oldProduct.get());
        if (productSave == null) {
            return false;
        }

        return true;
    }

    @Override
    public boolean delete(int id) {
        if (!productRepository.existsById(id)) {
            System.out.println("product not exist!!");
            return false;
        }
        Optional<Product> p = productRepository.findById(id);

        //Neu san pham do co trong chi tiet don hang thi khong cho xoa
        if (OrderDetailRepository.existsByProduct(id)){
            System.out.println("product exist in OrderDetail !!!!");
            return false;
        }

        productRepository.deleteById(id);

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
    public boolean stateProduct(int id, int state) {
        var product = productRepository.findById(id);
        if (product.isEmpty())
            return false;

        if (state == 1)
            product.get().setStateProduct(StateProduct.DISABLED);
        if (state == 0)
            product.get().setStateProduct(StateProduct.ACTIVED);

        productRepository.save(product.get());

        return true;
    }


}
