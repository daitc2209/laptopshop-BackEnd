package com.datn.laptopshop.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.datn.laptopshop.config.JavaMail;
import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.UserDto;
import com.datn.laptopshop.dto.request.EditProfileRequest;
import com.datn.laptopshop.dto.request.SearchUserRequest;
import com.datn.laptopshop.dto.request.SignInRequest;
import com.datn.laptopshop.dto.request.SignUpRequest;
import com.datn.laptopshop.service.IUserService;
import com.datn.laptopshop.utils.IdLogged;
import com.datn.laptopshop.utils.URL;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private JavaMail javaMail;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody SignInRequest u) throws Exception {
        System.out.println("login: "+u.toString());
        return userService.login(u);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody SignUpRequest u) {

        try {
            String token = UUID.randomUUID().toString();
            long tokenExpireAt = new Date().getTime() + TimeUnit.MINUTES.toMillis(5);
            var user = userService.register(u, token, tokenExpireAt);

            if(user != null) {
                String subject = "Account Verification";
                String verifyLink = "http://localhost:5173/auth/sign-up?token="+token;
                String content = "<p>Hello,</p>"
                        + "<p>Please click the link below to verify your registration:</p>"
                        + "<h3><a href='" + verifyLink + "'>VERIFY</a></h3>"
                        + "<p>Note: This link will expire in 5 minutes</p>";
                System.out.println("vao duoc gui mail: ");
                javaMail.sendEmail(user.getEmail(), subject, content);
                System.out.println("qua duoc gui mail");
                return ResponseHandler.responseBuilder("success",
                        "You have signed up successfully! Please check your email to verify your account",
                        HttpStatus.OK,"",0);
            }
        } catch (Exception e) {
            return ResponseHandler.responseBuilder("error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
        return ResponseHandler.responseBuilder("error","Something wrong in register UserController",
                HttpStatus.BAD_REQUEST,"",99);
    }

    @GetMapping("/register/verify")
    public ResponseEntity<Object> verify(@RequestParam("token") String token) {
        try {
            boolean valid = userService.checkRegisterToken(token);

            if(valid == true) {
                return ResponseHandler.responseBuilder("success",
                        "Congratulations, your account has been verified. Please sign in.",
                        HttpStatus.OK,"",0);
            }else {
                return ResponseHandler.responseBuilder("error",
                        "Sorry, we could not verify account. It maybe already verified, or verification code is incorrect.",
                        HttpStatus.OK,"",0);
            }
        } catch (Exception e) {
            return ResponseHandler.responseBuilder("error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("refreshToken!!!!!");
        userService.refreshToken(request, response);
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logout(@RequestParam("token") String token){
        try {
            var l = userService.logout(token);
            if (l)
                return ResponseHandler.responseBuilder("success", "Logout success", HttpStatus.OK,"",0);
            return ResponseHandler.responseBuilder("error", "Logout failed", HttpStatus.BAD_REQUEST,"",99);

        }catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @GetMapping("/user/profile")
    public ResponseEntity<Object> getProfile(){
        try{
            UserDto user = userService.findUserByEmail(IdLogged.getUser());
            if (user == null){
                user = userService.findUserByUsername(IdLogged.getUser());
            }
            Map m = new HashMap<>();
            if (user != null){
                EditProfileRequest profile = new EditProfileRequest();
                profile.setId(user.getId());
                profile.setEmail(user.getEmail());
                profile.setImg(user.getImg());
                profile.setAddress(user.getAddress());
                profile.setSex(user.getGender());
                profile.setBirthday(user.getDob());
                profile.setFullname(user.getFullname());
                profile.setPhone(user.getPhone());
                m.put("profile",profile);
            }
            return ResponseHandler.responseBuilder("success", "get profile user success", HttpStatus.OK,m,0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping(value = "/user/profile")
    public ResponseEntity<Object> editProfile(
            @RequestParam(value = "fileImage", required = false) MultipartFile fileImage,
            @RequestParam(value = "id") int id,
            @RequestParam(value = "fullname") String fullname,
            @RequestParam(value = "address") String address,
            @RequestParam(value = "sex") String sex,
            @RequestParam(value = "birthday") String birthday,
            @RequestParam(value = "email") String email,
            @RequestParam(value = "phone") String phone) {
        try {
            EditProfileRequest profile = new EditProfileRequest();
            profile.setId(id);
            profile.setFullname(fullname);
            profile.setAddress(address);
            profile.setSex(sex);
            profile.setBirthday(birthday);
            profile.setEmail(email);
            profile.setPhone(phone);
            UserDto u = userService.findbyId(profile.getId());
            if (u != null){
                boolean res = userService.update(profile, fileImage);
                if (res)
                    return ResponseHandler.responseBuilder("success", "post profile user success", HttpStatus.OK,"",0);
            }

            return ResponseHandler.responseBuilder("error", "post profile user failed !!", HttpStatus.OK,"",0);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/user/profile/change-password")
    public ResponseEntity<Object> changePassword(@RequestParam("oldPW") String oldPW,
                                                 @RequestParam("newPW") String newPW){
        try {
            if (oldPW == null || newPW == null || oldPW=="" || newPW=="" || oldPW == newPW)
                return ResponseHandler.responseBuilder("error", "request change pw null or oldPW = newPW", HttpStatus.BAD_REQUEST,"",99);

            var u = userService.findUserByEmail(IdLogged.getUser());
            if (u == null){
                u = userService.findUserByUsername(IdLogged.getUser());
            }
            if (u != null){
                boolean t = userService.changePW(u.getId(),oldPW, newPW);
                if (t)
                    return ResponseHandler.responseBuilder("success", "Change pw success", HttpStatus.OK,"",0);
            }

            return ResponseHandler.responseBuilder("error", "not found user", HttpStatus.BAD_REQUEST,"",99);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

}
