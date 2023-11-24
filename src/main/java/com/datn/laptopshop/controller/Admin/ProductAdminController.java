package com.datn.laptopshop.controller.Admin;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.ProductDto;
import com.datn.laptopshop.dto.UserDto;
import com.datn.laptopshop.dto.request.AddUserRequest;
import com.datn.laptopshop.dto.request.EditUserRequest;
import com.datn.laptopshop.dto.request.SearchProductRequest;
import com.datn.laptopshop.dto.request.SearchUserRequest;
import com.datn.laptopshop.enums.AuthenticationType;
import com.datn.laptopshop.enums.StateUser;
import com.datn.laptopshop.service.IBrandService;
import com.datn.laptopshop.service.ICategoryService;
import com.datn.laptopshop.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/admin/product")
public class ProductAdminController {
    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IBrandService brandService;

    private final String FOLDER_PATH="D:\\DATN\\laptopshop_VueJS\\laptopshop_vuejs\\src\\images\\product\\";

    @GetMapping
    public ResponseEntity<?> showProductPage(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "price", defaultValue = "-1") int price,
            @RequestParam(name = "discount", defaultValue = "-1") int discount,
            @RequestParam(name = "categoryName", defaultValue = "0") long categoryName,
            @RequestParam(name = "brandName", defaultValue = "0") long brandName) {

        try{
            SearchProductRequest search = new SearchProductRequest(name,price,discount,categoryName,brandName);
            System.out.println("search: "+search.toString());
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
            @RequestParam(value = "description") String description){
        try {
            if(discount >= 100)
                discount = 100;

            ProductDto productDto = new ProductDto();
            productDto.setName(name);
            productDto.setCategoryName(categoryName);
            productDto.setBrandName(brandName);
            productDto.setPrice(price);
            productDto.setDiscount(discount);
            productDto.setQuantity(quantity);
            productDto.setDescription(description);
            String nameImage = "";
            if (fileImage != null && !fileImage.isEmpty()){
                nameImage = UUID.randomUUID().toString().charAt(0)+ StringUtils.cleanPath(fileImage.getOriginalFilename());
                String filePath=FOLDER_PATH+nameImage;
                fileImage.transferTo(new File(filePath));
                productDto.setImg(nameImage);
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
    public ProductDto productApi(@PathVariable("id") long id) {
        return productService.findProductId(id);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> handleUpdate(
            @RequestParam(value = "fileImage", required = false) MultipartFile fileImage,
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "categoryName") String categoryName,
            @RequestParam(value = "brandName") String brandName,
            @RequestParam(value = "price") int price,
            @RequestParam(value = "discount") int discount,
            @RequestParam(value = "quantity") int quantity,
            @RequestParam(value = "description") String description){
        try{
            if(discount >= 100)
                discount = 100;

            ProductDto productDto = new ProductDto();
            productDto.setId(id);
            productDto.setName(name);
            productDto.setCategoryName(categoryName);
            productDto.setBrandName(brandName);
            productDto.setPrice(price);
            productDto.setDiscount(discount);
            productDto.setQuantity(quantity);
            productDto.setDescription(description);
            if (fileImage != null && !fileImage.isEmpty()) {
                String nameImage = "";
                nameImage = UUID.randomUUID().toString().charAt(0)+ StringUtils.cleanPath(fileImage.getOriginalFilename());
                String filePath = FOLDER_PATH +nameImage;
                fileImage.transferTo(new File(filePath));
                productDto.setImg(nameImage);
            }

            System.out.println("productdto: "+productDto.toString());
            boolean res = productService.update(productDto);
            if (res)
                return ResponseHandler.responseBuilder("success", " edit product successfully", HttpStatus.OK,"",0);

            return ResponseHandler.responseBuilder("error", " edit product failed !!", HttpStatus.OK,"",0);

        }catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteProduct(@RequestParam("id") long id){
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
}
