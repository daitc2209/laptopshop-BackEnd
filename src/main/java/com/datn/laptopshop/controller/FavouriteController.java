package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.service.IFavouriteService;
import com.datn.laptopshop.utils.IdLogged;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favour")
public class FavouriteController {

    @Autowired
    IFavouriteService favouriteService;

    @GetMapping
    public ResponseEntity<Object> getAllFavour(){
        try{
            Map m = new HashMap<>();
            var f = favouriteService.findAll(IdLogged.getUser());
            m.put("listFavour",f);
            return ResponseHandler.responseBuilder("Message","Success",
                    HttpStatus.OK,m,1);
        }catch (Exception e){
            return ResponseHandler.responseBuilder("Error",
                    "Get favour failed !!!",
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/add-to-favour")
    public ResponseEntity<Object> addProductToFavor(@RequestParam("productId") int id){
        try {
            var f = favouriteService.insert(IdLogged.getUser(),id);
            if (f)
                return ResponseHandler.responseBuilder("Message","Success",
                        HttpStatus.OK,"",1);
            return ResponseHandler.responseBuilder("Message","Error",
                    HttpStatus.OK,"The Product exist in favour !!",1);
        }catch (Exception e){
            return ResponseHandler.responseBuilder("Error",
                    "Add to favour failed !!!",
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/delete-favour")
    public ResponseEntity<Object> deleteProductToFavor(@RequestParam("id") int id){
        try {
            var f = favouriteService.delete(id);

            if (f)
                return ResponseHandler.responseBuilder("Message","Success",
                        HttpStatus.OK,"",1);
            return ResponseHandler.responseBuilder("Message","Error",
                    HttpStatus.OK,"The favour is not existed !!",1);
        }catch (Exception e){
            return ResponseHandler.responseBuilder("Error",
                    "Delete product from favour failed !!!",
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }
}
