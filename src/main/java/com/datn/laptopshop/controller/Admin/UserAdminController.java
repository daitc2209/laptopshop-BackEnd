package com.datn.laptopshop.controller.Admin;

import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.UserDto;
import com.datn.laptopshop.dto.request.AddUserRequest;
import com.datn.laptopshop.dto.request.EditUserRequest;
import com.datn.laptopshop.dto.request.SearchUserRequest;
import com.datn.laptopshop.enums.AuthenticationType;
import com.datn.laptopshop.enums.StateUser;
import com.datn.laptopshop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/user")
public class UserAdminController {
    @Autowired
    private IUserService userService;

    private final String FOLDER_PATH="D:\\DATN\\laptopshop_VueJS\\laptopshop_vuejs\\src\\images\\user\\";

    @GetMapping
    public ResponseEntity<?> getListUser(
            @RequestParam(name = "fullname", defaultValue = "") String fullname,
            @RequestParam(name = "sex", defaultValue = "") String sex,
            @RequestParam(name = "address", defaultValue = "") String address,
            @RequestParam(name = "email", defaultValue = "") String email,
            @RequestParam(name = "stateUser", defaultValue = "") StateUser stateUser,
            @RequestParam(name = "authType", defaultValue = "") AuthenticationType authType,
            @RequestParam(name = "role", defaultValue = "0") Long role,
            @RequestParam(name = "page", defaultValue = "1") int page){
        try{
            SearchUserRequest search = new SearchUserRequest(fullname,sex,address,email,stateUser,authType,role);

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
    public UserDto userApi(@PathVariable("id") long id) {
        return userService.findbyId(id);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> handleUpdate(
            @RequestParam(value = "fileImage", required = false) MultipartFile fileImage,
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "fullname") String fullname,
            @RequestParam(value = "address") String address,
            @RequestParam(value = "sex") String sex,
            @RequestParam(value = "birthday") String birthday,
            @RequestParam(value = "stateUser") StateUser stateUser,
            @RequestParam(value = "authType") AuthenticationType authType,
            @RequestParam(value = "role") Long role,
            @RequestParam(value = "phone") String phone){
        try{
            EditUserRequest edit = new EditUserRequest();
            edit.setId(id);
            edit.setFullname(fullname);
            edit.setAddress(address);
            edit.setSex(sex);
            edit.setBirthday(birthday);
            edit.setStateUser(stateUser);
            edit.setAuthType(authType);
            edit.setRole(role);
            edit.setPhone(phone);
            System.out.println("edit: "+edit.toString());
            String nameImage = "";

            UserDto u = userService.findbyId(edit.getId());
            if (u != null) {
                if (fileImage != null && !fileImage.isEmpty()) {

                    nameImage = UUID.randomUUID().toString().charAt(0)+ StringUtils.cleanPath(fileImage.getOriginalFilename());

                    //tao duong dan den thu muc fontend , tao random truoc ten file anh
                    String filePath = FOLDER_PATH +nameImage;

                    //chuyen file anh do sang thu muc fontend
                    fileImage.transferTo(new File(filePath));

                    edit.setImg(nameImage);
                    System.out.println("1");

                }
                System.out.println("2");
                boolean res = userService.update(edit);
                System.out.println("15");

                if (res)
                    return ResponseHandler.responseBuilder("success", "post edit user successfully", HttpStatus.OK,"",0);
            }
            return ResponseHandler.responseBuilder("error", "post edit user failed !!", HttpStatus.OK,"",0);

        }catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/lock")
    public ResponseEntity<?> lockUser(@RequestParam("id") long id){
        try {
            boolean res = userService.lock(id);
            if (res){
                return ResponseHandler.responseBuilder("success", "Lock user successfully!\"", HttpStatus.OK,"",0);
            }
            return ResponseHandler.responseBuilder("error", "Lock user failed!", HttpStatus.OK,"",0);
        }catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/unlock")
    public ResponseEntity<?> unlockUser(@RequestParam("id") long id){
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
}
