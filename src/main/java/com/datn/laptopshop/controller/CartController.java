package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.dto.CartItem;
import com.datn.laptopshop.service.ICartService;
import com.datn.laptopshop.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private IProductService productService;

    @Autowired
    private ICartService cartService;

    @PostMapping("/add-to-cart")
    public ResponseEntity<Object> addToCart(
            @RequestParam(name = "productId") int productId,
            @RequestParam(name = "num",defaultValue = "1") int num){
        try {
            ProductDto product = productService.findProductId(productId);
            if (product == null) {
                return ResponseHandler.responseBuilder("Error","Invalid product ID", HttpStatus.BAD_REQUEST,"",99);
            }
            if (product.getQuantity() < num)
                return ResponseHandler.responseBuilder("Error","Out of stock", HttpStatus.BAD_REQUEST,"",99);

            int numProduct = num;
            CartItem cartItem = new CartItem(
                    productId,
                    product.getName(),
                    product.getPrice(),
                    product.getDiscount(),
                    product.getImg(),
                    product.getQuantity(),
                    numProduct,
                    numProduct * (product.getPrice() - (product.getPrice() * product.getDiscount() / 100))
            );
            CartItem cartItems = cartService.findCartItem(productId);
            if (cartItems != null){
                if(product.getQuantity() - cartItems.getNumProduct() - num >=0)
                {
                    return cartService.addItem(cartItem);
                }
                else
                    return ResponseHandler.responseBuilder("Error","Out of stock", HttpStatus.BAD_REQUEST,"",99);
            }
            else {
                if (product.getQuantity() - num >=0)
                    return cartService.addItem(cartItem);
                else
                    return ResponseHandler.responseBuilder("Error","Out of stock", HttpStatus.BAD_REQUEST,"",99);
            }

        } catch (Exception e) {
            return ResponseHandler.responseBuilder("Error",e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getCartItems() {
        try {
            var cartItems = cartService.getAllItems();
            double totalMoney = cartService.getTotalMoney();
            int totalQuantity = cartService.getTotalQuantity();
            Map m = new HashMap<>();
            m.put("listCartItems", cartItems);
            m.put("totalMoney", totalMoney);
            m.put("totalQuantity", totalQuantity);

            return ResponseHandler.responseBuilder("Message","Success", HttpStatus.OK,m,0);
        } catch (Exception e) {
            return ResponseHandler.responseBuilder("Error",e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @GetMapping("/edit-cart/{productId}/{num}")
    public ResponseEntity<Object> editCartItem(
            @PathVariable("productId") int productId,
            @PathVariable("num") int num){
        try {
            CartItem cartItem = cartService.findCartItem(productId);
            if (cartItem != null){
                if (num == -1){
                    if (cartItem.getNumProduct() <= 1){
                        cartService.removeItem(productId);
                        return ResponseHandler.responseBuilder("Message","Remove item out of cart Success", HttpStatus.OK,"",0);
                    }
                    else
                        return cartService.editItem(productId, cartItem.getNumProduct()-1);
                }
                if (num == 1){
                    ProductDto productDto = productService.findProductId(productId);
                    if (productDto != null && productDto.getQuantity() - cartItem.getNumProduct() - num >= 0){
                        return cartService.editItem(productId, cartItem.getNumProduct()+1);
                    }
                    else{
                        return ResponseHandler.responseBuilder("Error","Out of stock", HttpStatus.BAD_REQUEST,"",99);
                    }
                }
            }
            else {
                return ResponseHandler.responseBuilder("Error","Not Item in cart", HttpStatus.BAD_REQUEST,"",99);
            }
        }catch (Exception e){
            return ResponseHandler.responseBuilder("Error","Error something in cart edit quantity", HttpStatus.BAD_REQUEST,"",99);
        }
        return ResponseHandler.responseBuilder("Error",
                "Co loi gi day khong on o edit item cart",
                HttpStatus.BAD_REQUEST,"",99);
    }

    @GetMapping("/delete-cart/{id}")
    public ResponseEntity<Object> deleteCartItem(@PathVariable("id") int productId){
        var o = cartService.findCartItem(productId);
        if (o != null){
            cartService.removeItem(productId);
            return ResponseHandler.responseBuilder("Message",
                    "Success",
                    HttpStatus.OK,"",0);
        }
        return ResponseHandler.responseBuilder("Error",
                "Delete cart fail !!!",
                HttpStatus.BAD_REQUEST,"",99);
    }

    @GetMapping("/clear-cart")
    public ResponseEntity<Object> clearCartItem(){

        cartService.clearItem();

        return ResponseHandler.responseBuilder("Message",
                "Clear Success",
                HttpStatus.OK,"",0);
    }

}
