package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.dto.FavoutiteDto;
import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.entity.Favourite;
import com.datn.laptopshop.repos.FavouriteRepository;
import com.datn.laptopshop.repos.ProductRepository;
import com.datn.laptopshop.repos.UserRepository;
import com.datn.laptopshop.service.IFavouriteService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FavouriteService implements IFavouriteService {

    @Autowired
    FavouriteRepository favouriteRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public List<FavoutiteDto> findAll(String username) {
        var user = userRepository.findUser(username);
        if (!user.isPresent())
            return null;
        var fav = favouriteRepository.findFavourByUser(user.get().getId());
        List<FavoutiteDto> list = new ArrayList<>();
        for (Favourite f : fav)
        {
            FavoutiteDto favDto = new FavoutiteDto();
            favDto.setId(f.getId());
            favDto.setProduct(new ProductDto().toProductDTO(f.getProduct()));
            list.add(favDto);
        }
        return list;
    }

    @Override
    public boolean insert(String username, long product_id) {
        var user = userRepository.findUser(username);
        var product = productRepository.findById(product_id);
        if (!user.isPresent() || !product.isPresent())
            return false;
        if (favouriteRepository.existsProductInFavour(user.get().getId(), product_id))
        {
            System.out.println("The Product exist in Favour !!");
            return false;
        }
        Favourite fav = new Favourite();
        fav.setUser(user.get());
        fav.setProduct(product.get());
        favouriteRepository.save(fav);
        return true;
    }

    @Override
    public boolean delete(long id) {
        if (favouriteRepository.findById(id) == null)
        {
            System.out.println("The favour not exist !!");
            return false;
        }

        // Clear data based on input
        favouriteRepository.deleteById(id);

        // If the deleted data still exists, return false
        if (favouriteRepository.existsById(id)) {
            return false;
        }

        return true;
    }
}
