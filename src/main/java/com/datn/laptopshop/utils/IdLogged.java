package com.datn.laptopshop.utils;

import com.datn.laptopshop.config.Oauth2.CustomOAuth2User;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class IdLogged {

    public static String getUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String id=null;
        if (principal instanceof User){
            id = ((User) principal).getUsername();
        }
        return id;
    }
}
