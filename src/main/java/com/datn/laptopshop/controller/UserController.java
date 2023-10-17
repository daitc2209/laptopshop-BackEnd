package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.JavaMail;
import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.request.SignInRequest;
import com.datn.laptopshop.dto.request.SignUpRequest;
import com.datn.laptopshop.entity.User;
import com.datn.laptopshop.service.IUserService;
import com.datn.laptopshop.utils.URL;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
    public ResponseEntity<Object> register(@RequestBody SignUpRequest u, HttpServletRequest request) {

        try {
            Map m = new HashMap<>();
            String token = UUID.randomUUID().toString();
            long tokenExpireAt = new Date().getTime() + TimeUnit.MINUTES.toMillis(5);
            var user = userService.register(u, token, tokenExpireAt);
            if(user != null) {
                String subject = "Account Verification";

                String verifyLink = URL.getSiteURL(request) + "/api/register/verify?token="+token;
                String content = "<p>Hello,</p>"
                        + "<p>Please click the link below to verify your registration:</p>"
                        + "<h3><a href='" + verifyLink + "'>VERIFY</a></h3>"
                        + "<p>Note: This link will expire in 5 minutes</p>";

                javaMail.sendEmail(user.getEmail(), subject, content);

                m.put("success","You have signed up successfully! Please check your email to verify your account");
                return ResponseHandler.responseBuilder("success",
                        "You have signed up successfully! Please check your email to verify your account",
                        HttpStatus.OK,"",0);
            }
        } catch (Exception e) {
            return ResponseHandler.responseBuilder("error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
        return null;
    }

    @GetMapping("/register/verify")
    public ResponseEntity<Object> verify(@RequestParam("token") String token) {
        try {
            boolean valid = userService.checkRegisterToken(token);

            if(valid) {
                return ResponseHandler.responseBuilder("success",
                        "Congratulations, your account has been verified. Please sign in.",
                        HttpStatus.OK,"",0);
            }else {
                return ResponseHandler.responseBuilder("error",
                        "Sorry, we could not verify account. It maybe already verified, or verification code is incorrect.",
                        HttpStatus.BAD_REQUEST,"",1);
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
}
