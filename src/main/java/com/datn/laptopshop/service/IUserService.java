package com.datn.laptopshop.service;

import com.datn.laptopshop.dto.UserDto;
import com.datn.laptopshop.dto.request.*;
import com.datn.laptopshop.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

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

    UserDto findUserByUsername(String name);

    UserDto findbyId(int id);

    boolean update(EditProfileRequest profile, MultipartFile fileImage);
    boolean update(EditUserRequest edit, MultipartFile fileImage);

    boolean changePW(int id,String oldPW, String newPW);

    boolean forgotPW(String email, String token, long tokenExpireAt);

    boolean resetPW(String token, String newPW);

    boolean checkResetPWToken(String token);

    boolean logout(String token);

    Page<UserDto> findAll(int page, int limit, SearchUserRequest search);

    boolean insert(AddUserRequest addUserRequest);

    boolean lock(int id, String username);

    boolean unlock(int id);

    boolean delete(int id, String username);
}
