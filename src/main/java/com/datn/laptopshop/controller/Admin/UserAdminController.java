package com.datn.laptopshop.controller.Admin;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.UserDto;
import com.datn.laptopshop.dto.request.AddUserRequest;
import com.datn.laptopshop.dto.request.EditUserRequest;
import com.datn.laptopshop.dto.request.SearchUserRequest;
import com.datn.laptopshop.enums.AuthenticationType;
import com.datn.laptopshop.enums.StateUser;
import com.datn.laptopshop.service.IUserService;
import com.datn.laptopshop.utils.IdLogged;
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
@RequestMapping("/api/admin/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {
    @Autowired
    private IUserService userService;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping
    public ResponseEntity<?> getListUser(
            @RequestParam(name = "fullname", defaultValue = "") String fullname,
            @RequestParam(name = "sex", defaultValue = "") String sex,
            @RequestParam(name = "address", defaultValue = "") String address,
            @RequestParam(name = "email", defaultValue = "") String email,
            @RequestParam(name = "stateUser", defaultValue = "") StateUser stateUser,
            @RequestParam(name = "authType", defaultValue = "") AuthenticationType authType,
            @RequestParam(name = "page", defaultValue = "1") int page){
        try{
            SearchUserRequest search = new SearchUserRequest(fullname,sex,address,email,stateUser,2,authType);

            int limit = 5;
            Map m = new HashMap<>();
            var listUser = userService.findAll(page, limit,search);

            m.put("listUser",listUser);
            m.put("currentPage",page);

            return ResponseHandler.responseBuilder
                    ("success", "Get list user success", HttpStatus.OK,m,0);

        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> handleCreate(@RequestBody AddUserRequest formAddUser){
        try {
            var u = userService.insert(formAddUser);
            if (u)
                return ResponseHandler.responseBuilder
                        ("success", "Create user success", HttpStatus.OK,"",0);

            return ResponseHandler.responseBuilder
                    ("error", "Create user failed !!!", HttpStatus.OK,"",0);

        }catch (Exception e)
        {
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @GetMapping("/edit/{id}")
    public UserDto userApi(@PathVariable("id") int id) {
        return userService.findbyId(id);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> handleUpdate(
            @RequestParam(value = "fileImage", required = false) MultipartFile fileImage,
            @RequestParam(value = "id") int id,
            @RequestParam(value = "fullname") String fullname,
            @RequestParam(value = "address") String address,
            @RequestParam(value = "sex") String sex,
            @RequestParam(value = "birthday") String birthday,
            @RequestParam(value = "stateUser") StateUser stateUser,
            @RequestParam(value = "phone") String phone){
        try{
            EditUserRequest edit = new EditUserRequest();
            edit.setId(id);
            edit.setFullname(fullname);
            edit.setAddress(address);
            edit.setSex(sex);
            edit.setBirthday(birthday);
            edit.setStateUser(stateUser);
            edit.setPhone(phone);
            UserDto u = userService.findbyId(edit.getId());
            if (u != null) {
                boolean res = userService.update(edit, fileImage);

                if (res)
                    return ResponseHandler.responseBuilder("success", "post edit user successfully", HttpStatus.OK,"",0);
            }
            return ResponseHandler.responseBuilder("error", "post edit user failed !!", HttpStatus.OK,"",0);

        }catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/lock")
    public ResponseEntity<?> lockUser(@RequestParam("id") int id){
        try {
            boolean res = userService.lock(id, IdLogged.getUser());
            if (res){
                return ResponseHandler.responseBuilder("success", "Lock user successfully!\"", HttpStatus.OK,"",0);
            }
            return ResponseHandler.responseBuilder("error", "Lock user failed!", HttpStatus.OK,"",0);
        }catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/unlock")
    public ResponseEntity<?> unlockUser(@RequestParam("id") int id){
        try {
            boolean res = userService.unlock(id);
            if (res){
                return ResponseHandler.responseBuilder("success", "Unlock user successfully!\"", HttpStatus.OK,"",0);
            }
            return ResponseHandler.responseBuilder("error", "Unlock user failed!", HttpStatus.OK,"",0);
        }catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam("id") int id){
        try {
            boolean res = userService.delete(id, IdLogged.getUser());
            if (res){
                return ResponseHandler.responseBuilder("success", "Delete user successfully!\"", HttpStatus.OK,"",0);
            }
            return ResponseHandler.responseBuilder("error", "Delete user failed!", HttpStatus.OK,"",0);
        }catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @GetMapping("/getAdmin")
    public ResponseEntity<?> getListAdmin(
            @RequestParam(name = "fullname", defaultValue = "") String fullname,
            @RequestParam(name = "sex", defaultValue = "") String sex,
            @RequestParam(name = "address", defaultValue = "") String address,
            @RequestParam(name = "email", defaultValue = "") String email,
            @RequestParam(name = "stateUser", defaultValue = "") StateUser stateUser,
            @RequestParam(name = "authType", defaultValue = "") AuthenticationType authType,
            @RequestParam(name = "page", defaultValue = "1") int page){
        try{
            SearchUserRequest search = new SearchUserRequest(fullname,sex,address,email,stateUser,1,authType);

            int limit = 4;
            Map m = new HashMap<>();
            var listUser = userService.findAll(page, limit,search);

            m.put("listUser",listUser);
            m.put("currentPage",page);

            return ResponseHandler.responseBuilder
                    ("success", "Get list user success", HttpStatus.OK,m,0);

        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }
}
