package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.JavaMail;
import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class ForgotPasswordController {
    @Autowired
    private IUserService userService;
    @Autowired
    private JavaMail javaMail;

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<Object> handleRequestForgorPW(@RequestParam("email") String email){
        try {
            String token = UUID.randomUUID().toString();
            long tokenExpireAt = new Date().getTime() + TimeUnit.MINUTES.toMillis(5);

            boolean t = userService.forgotPW(email,token, tokenExpireAt);
            if (t){
                String subject = "Password Reset Authentication";
                String resetPasswordLink = "http://localhost:5173/auth/reset-password?token=" + token;
                String content = "<p>Hello,</p>"
                        + "<p>Please click the link below to change your password:</p>"
                        + "<h3><a href=\"" + resetPasswordLink + "\">Change my password</a></h3>"
                        + "<p>Note: This link will expire in 5 minutes</p>";
                javaMail.sendEmail(email, subject, content);

                return ResponseHandler.responseBuilder("success", "We have sent a reset password link to your email. Please check.", HttpStatus.OK,"",0);
            }

            return ResponseHandler.responseBuilder("error", "not found user", HttpStatus.OK,"",99);
        }
        catch (Exception e){
            return ResponseHandler.responseBuilder("error", e.getMessage(), HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @GetMapping("/auth/reset-password")
    public ResponseEntity<Object> checkTokenPW(@RequestParam("token") String token) {
        try {
            boolean valid = userService.checkResetPWToken(token);

            if(valid) {
                return ResponseHandler.responseBuilder("success",
                        "",
                        HttpStatus.OK,"",0);
            }
            return ResponseHandler.responseBuilder("error",
                    "Invalid Token.",
                    HttpStatus.OK,"",0);

        } catch (Exception e) {
            return ResponseHandler.responseBuilder("error",e.getMessage(),
                    HttpStatus.BAD_REQUEST,"",99);
        }
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<?> resetPW(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "newPW") String newPW){
        try {
            var res = userService.resetPW(token,newPW);
            if (res)
                return ResponseHandler.responseBuilder("success", "Reset password success", HttpStatus.OK,"",0);
            return ResponseHandler.responseBuilder("error", "Reset Password failed", HttpStatus.OK,"",99);

        }
        catch(Exception e){
            return ResponseHandler.responseBuilder("error", "Reset Password failed", HttpStatus.BAD_REQUEST,e.getMessage(),99);
        }
    }
}
