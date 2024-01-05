package com.datn.laptopshop.controller.Admin;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.BrandDto;
import com.datn.laptopshop.dto.CategoryDto;
import com.datn.laptopshop.service.IBrandService;
import com.datn.laptopshop.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/brand")
@PreAuthorize("hasRole('ADMIN')")
public class BrandAdminController {
    @Autowired
    private IBrandService brandService;
    @GetMapping("/findAll")
    public ResponseEntity<?> findAll(){
        Map m = new HashMap<>();
        m.put("brands", brandService.findAll());
        return ResponseHandler.responseBuilder("success","get all Successfully !!!!!",
                HttpStatus.OK,m,0);
    }

    @GetMapping
    public ResponseEntity<?> brandsPage(
            @RequestParam(name = "page",defaultValue = "1") int page,
            @RequestParam(value = "search", defaultValue = "") String search){
        try {
            int limit = 8;
            Map m = new HashMap<>();
            var listBrands = brandService.findAll(page,limit,search);
            if (listBrands != null){
                m.put("listBrands", listBrands);
                m.put("currentPage", page);
                m.put("totalPage", listBrands.getTotalPages());
            }
            return ResponseHandler.responseBuilder("message","get listBrands successfully !!",
                    HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("err",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> handleCreate(@RequestParam(value = "name") String name,
                                          @RequestParam(value = "fileImage", required = false) MultipartFile img) {
        try {
            boolean res = brandService.insert(name, img);
            if(!res) {
                return ResponseHandler.responseBuilder("err","Create Failed !!!!!",
                        HttpStatus.OK,"",99);
            }else {
                return ResponseHandler.responseBuilder("success","Create Successfully !!!!!",
                        HttpStatus.OK,"",0);
            }
        } catch (Exception e) {
            return ResponseHandler.responseBuilder("err",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> brandApi(@PathVariable("id") int id) {
        Map m = new HashMap<>();
        m.put("brandDto", brandService.findById(id));
        return ResponseHandler.responseBuilder("success","get edit Successfully !!!!!",
                HttpStatus.OK,m,0);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> handleUpdate(@RequestParam(value = "id") int id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "fileImage", required = false) MultipartFile img) {
        try {
            boolean res = brandService.update(id,name, img);

            if(!res) {
                return ResponseHandler.responseBuilder("err","Update Failed !!!!!",
                        HttpStatus.BAD_REQUEST,"",99);
            }else {
                return ResponseHandler.responseBuilder("success","Update Successfully !!!!!",
                        HttpStatus.OK,"",0);
            }
        } catch (Exception e) {
            return ResponseHandler.responseBuilder("err",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteBrands(@RequestParam("id") int id){
        try{
            boolean res = brandService.delete(id);
            if (res){
                return ResponseHandler.responseBuilder("success","Delete Successfully !!!!!",
                        HttpStatus.OK,"",0);
            }
            return ResponseHandler.responseBuilder("err","Delete Failed !!!!!",
                    HttpStatus.OK,"",99);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("err",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }
}
