package com.datn.laptopshop.controller.Admin;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.BrandDto;
import com.datn.laptopshop.dto.CategoryDto;
import com.datn.laptopshop.service.IBrandService;
import com.datn.laptopshop.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/brand")
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
    public ResponseEntity<?> categoriesPage(
            @RequestParam(name = "page",defaultValue = "1") int page,
            @RequestParam(value = "search", defaultValue = "") String search){
        try {
            int limit = 4;
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
    public ResponseEntity<?> handleCreate(@RequestBody BrandDto brandDto) {

        try {
            boolean res = brandService.insert(brandDto);
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
    public ResponseEntity<?> brandApi(@PathVariable("id") long id) {
        Map m = new HashMap<>();
        m.put("brandDto", brandService.findById(id));
        return ResponseHandler.responseBuilder("success","get edit Successfully !!!!!",
                HttpStatus.OK,m,0);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> handleUpdate(@RequestBody BrandDto brandDto) {
        try {
            boolean res = brandService.update(brandDto);

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
    public ResponseEntity<?> deleteBrands(@RequestParam("id") long id){
        try{
            boolean res = brandService.delete(id);
            if (res){
                return ResponseHandler.responseBuilder("success","Delete Successfully !!!!!",
                        HttpStatus.OK,"",0);
            }
            return ResponseHandler.responseBuilder("err","Delete Failed !!!!!",
                    HttpStatus.BAD_REQUEST,"",99);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("err",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }
}
