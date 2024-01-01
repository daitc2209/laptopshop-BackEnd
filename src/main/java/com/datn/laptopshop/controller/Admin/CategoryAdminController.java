package com.datn.laptopshop.controller.Admin;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.CategoryDto;
import com.datn.laptopshop.dto.NewsDto;
import com.datn.laptopshop.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController("")
@RequestMapping("/api/admin/category")
@PreAuthorize("hasRole('ADMIN')")
public class CategoryAdminController {
    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll(){
        Map m = new HashMap<>();
        m.put("categories", categoryService.findAll());
        return ResponseHandler.responseBuilder("success","get all categories Successfully !!!!!",
                HttpStatus.OK,m,0);
    }

    @GetMapping
    public ResponseEntity<?> categoriesPage(
            @RequestParam(name = "page",defaultValue = "1") int page,
            @RequestParam(value = "search", defaultValue = "") String search){
        try {
            int limit = 6;
            Map m = new HashMap<>();
            var listCategories = categoryService.findAll(page,limit,search);
            if (listCategories != null){
                m.put("listCategories", listCategories);
                m.put("currentPage", page);
                m.put("totalPage", listCategories.getTotalPages());

            }
            return ResponseHandler.responseBuilder("message","get categories successfully !!",
                    HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("err",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> handleCreate(@RequestBody CategoryDto categoryDto) {

        try {
            boolean res = categoryService.insert(categoryDto);
            if(!res) {
                return ResponseHandler.responseBuilder("err","Create Failed !!!!!",
                        HttpStatus.BAD_REQUEST,"",99);
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
    public ResponseEntity<?> categoriesApi(@PathVariable("id") int id) {
        Map m = new HashMap<>();
        m.put("categoryDto", categoryService.findById(id));
        return ResponseHandler.responseBuilder("success","get edit Successfully !!!!!",
                HttpStatus.OK,m,0);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> handleUpdate(@RequestBody CategoryDto categoryDto) {
        try {
            boolean res = categoryService.update(categoryDto);

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
    public ResponseEntity<?> deleteCategory(@RequestParam("id") int id){
        try{
            boolean res = categoryService.delete(id);
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
