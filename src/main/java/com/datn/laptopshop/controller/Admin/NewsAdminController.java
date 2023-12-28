package com.datn.laptopshop.controller.Admin;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.NewsDto;
import com.datn.laptopshop.service.ICategoryService;
import com.datn.laptopshop.service.INewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class NewsAdminController {

    @Autowired
    private INewsService newsService;

    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/news")
    public ResponseEntity<?> newsPage(
            @RequestParam(name = "page",defaultValue = "1") int page,
            @RequestParam(value = "search", defaultValue = "") String search){
        try {
            int limit = 4;
            Map m = new HashMap<>();
            var listNews = newsService.findAll(page,limit,search);
            if (listNews != null){
                m.put("news", listNews);
                m.put("currentPage", page);
                m.put("totalPage", listNews.getTotalPages());
                m.put("category", categoryService.findAll());

            }
            return ResponseHandler.responseBuilder("message","success get new from admin",
                    HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("err",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/news/add")
    public ResponseEntity<?> handleCreate(@RequestBody NewsDto newDto) {

        try {
            System.out.println("newDto add: "+newDto.toString());
            NewsDto newReponse = newsService.insert(newDto);
            System.out.println("da vao duoc ");
            System.out.println("res: "+newReponse.toString());

            if(newReponse == null) {
                return ResponseHandler.responseBuilder("err","Create Failed !!!!!",
                        HttpStatus.BAD_REQUEST,"",99);
            }else {
                return ResponseHandler.responseBuilder("success","Create Successed !!!!!",
                        HttpStatus.OK,"",0);
            }
        } catch (Exception e) {
            return ResponseHandler.responseBuilder("err",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @GetMapping("/news/edit/{id}")
    public ResponseEntity<?> newApi(@PathVariable("id") int id) {
        Map m = new HashMap<>();
        m.put("newsDto", newsService.findById(id));
        return ResponseHandler.responseBuilder("success","get edit Successed !!!!!",
                HttpStatus.OK,m,0);
    }

    @PostMapping("/news/edit")
    public ResponseEntity<?> handleUpdate(@RequestBody NewsDto newDto) {
        try {
            boolean newReponse = newsService.update(newDto);

            if(!newReponse) {
                return ResponseHandler.responseBuilder("err","Update Failed !!!!!",
                        HttpStatus.BAD_REQUEST,"",99);
            }else {
                return ResponseHandler.responseBuilder("success","Update Successed !!!!!",
                        HttpStatus.OK,"",0);
            }
        } catch (Exception e) {
            return ResponseHandler.responseBuilder("err",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/news/delete")
    public ResponseEntity<?> deleteNews(@RequestParam("id") int id){
        try{
            boolean reponse = newsService.delete(id);
            if (reponse){
                return ResponseHandler.responseBuilder("success","Delete Successed !!!!!",
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
