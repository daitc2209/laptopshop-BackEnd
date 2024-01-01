package com.datn.laptopshop.controller.Admin;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.dto.request.SearchProductRequest;
import com.datn.laptopshop.enums.StateOrder;
import com.datn.laptopshop.enums.StateProduct;
import com.datn.laptopshop.service.IBrandService;
import com.datn.laptopshop.service.ICategoryService;
import com.datn.laptopshop.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/admin/product")
@PreAuthorize("hasRole('ADMIN')")
public class ProductAdminController {
    @Autowired
    private IProductService productService;

    @Autowired
    private Cloudinary cloudinary;

    private final String FOLDER_PATH="D:\\DATN\\laptopshop_VueJS\\laptopshop_vuejs\\src\\images\\product\\";

    @GetMapping
    public ResponseEntity<?> showProductPage(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "search_text", defaultValue = "") String text,
            @RequestParam(name = "categoryId", defaultValue = "0") int categoryId,
            @RequestParam(name = "brandId", defaultValue = "0") int brandId) {

        try{
            SearchProductRequest search = new SearchProductRequest(text,categoryId,brandId);
            int limit = 5;
            Map m = new HashMap<>();
            var listProduct = productService.findAll(page, limit,search);

            m.put("listProduct",listProduct);
            m.put("currentPage",page);

            return ResponseHandler.responseBuilder
                    ("success", "Get list Product successfully", HttpStatus.OK,m,0);

        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> handleCreate(
            @RequestParam(value = "fileImage", required = false) MultipartFile fileImage,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "categoryName") String categoryName,
            @RequestParam(value = "brandName") String brandName,
            @RequestParam(value = "price", defaultValue = "0") int price,
            @RequestParam(value = "discount", defaultValue = "0") int discount,
            @RequestParam(value = "quantity", defaultValue = "0") int quantity,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "state", defaultValue = "DISABLED") String state){
        try {
            if(discount >= 100)
                discount = 100;

            StateProduct stateProduct;
            try{
                stateProduct = StateProduct.valueOf(state.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                stateProduct = null;
            }

            ProductDto productDto = new ProductDto();
            productDto.setName(name);
            productDto.setCategoryName(categoryName);
            productDto.setBrandName(brandName);
            productDto.setPrice(price);
            productDto.setDiscount(discount);
            productDto.setQuantity(quantity);
            productDto.setDescription(description);
            productDto.setState(stateProduct);
            String nameImage = "";
            try{
                Map r = cloudinary.uploader().upload(fileImage.getBytes(), ObjectUtils.asMap("folder","images/product"));
                nameImage = (String) r.get("url");
                productDto.setImg(nameImage);
            }catch (Exception e){
                productDto.setImg(null);
                e.printStackTrace();
            }
            var p = productService.insert(productDto);
            if (p)
                return ResponseHandler.responseBuilder
                        ("success", "Create product success", HttpStatus.OK,"",0);

            return ResponseHandler.responseBuilder
                    ("error", "Create product failed !!!", HttpStatus.OK,"",0);

        }catch (Exception e)
        {
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }


    @GetMapping("/edit/{id}")
    public ProductDto productApi(@PathVariable("id") int id) {
        return productService.findProductId(id);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> handleUpdate(
            @RequestParam(value = "fileImage", required = false) MultipartFile fileImage,
            @RequestParam(value = "id") int id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "categoryName") String categoryName,
            @RequestParam(value = "brandName") String brandName,
            @RequestParam(value = "price") int price,
            @RequestParam(value = "discount") int discount,
            @RequestParam(value = "quantity") int quantity,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "state", defaultValue = "DISABLED") String state){
        try{
            if(discount >= 100)
                discount = 100;
            StateProduct stateProduct;
            try{
                stateProduct = StateProduct.valueOf(state.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                stateProduct = null;
            }

            ProductDto productDto = new ProductDto();
            productDto.setId(id);
            productDto.setName(name);
            productDto.setCategoryName(categoryName);
            productDto.setBrandName(brandName);
            productDto.setPrice(price);
            productDto.setDiscount(discount);
            productDto.setQuantity(quantity);
            productDto.setDescription(description);
            productDto.setState(stateProduct);
            try{
                Map r = cloudinary.uploader().upload(fileImage.getBytes(), ObjectUtils.asMap("folder","images/product"));
                String nameImage = (String) r.get("url");
                productDto.setImg(nameImage);
            }catch (Exception e){
                e.printStackTrace();
            }

            boolean res = productService.update(productDto);
            if (res)
                return ResponseHandler.responseBuilder("success", " edit product successfully", HttpStatus.OK,"",0);

            return ResponseHandler.responseBuilder("error", " edit product failed !!", HttpStatus.OK,"",0);

        }catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteProduct(@RequestParam("id") int id){
        try {
            boolean res = productService.delete(id);
            if (res){
                return ResponseHandler.responseBuilder("success", "Remove product successfully!\"", HttpStatus.OK,"",0);
            }
            return ResponseHandler.responseBuilder("error", "Remove product failed!", HttpStatus.OK,"",0);
        }catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/lock_unlock")
    public ResponseEntity<?> lock_unlock(@RequestParam("id") int id, @RequestParam("state") int state){
        try {
                boolean res = productService.stateProduct(id,state);
                if (res){
                    return ResponseHandler.responseBuilder("success", "Unlock user successfully!\"", HttpStatus.OK,"",0);
                }
                return ResponseHandler.responseBuilder("error", "Unlock user failed!", HttpStatus.OK,"",1);

        }catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }
}
