package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.UserDto;
import com.datn.laptopshop.dto.request.EditProfileRequest;
import com.datn.laptopshop.dto.request.SignInRequest;
import com.datn.laptopshop.dto.request.SignUpRequest;
import com.datn.laptopshop.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface IUserService {

    ResponseEntity<Object> login(SignInRequest u) throws Exception;

    UserDto register(SignUpRequest u, String token, long tokenExpireAt) throws Exception;

    boolean checkRegisterToken(String token) throws Exception;

    void saveUserToken(User user, String jwtToken);

    void revokeAllUserTokens(User user);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    UserDto findUserByEmail(String name);

    User findUserByEmailGG(String name);

    UserDto findbyId(long id);

    boolean update(EditProfileRequest profile);

    boolean changePW(long id,String oldPW, String newPW);
}
