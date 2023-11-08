package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.config.JWTService;
import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.UserDto;
import com.datn.laptopshop.dto.request.EditProfileRequest;
import com.datn.laptopshop.dto.request.SignInRequest;
import com.datn.laptopshop.dto.request.SignUpRequest;
import com.datn.laptopshop.entity.Role;
import com.datn.laptopshop.entity.Token;
import com.datn.laptopshop.entity.User;
import com.datn.laptopshop.enums.AuthenticationType;
import com.datn.laptopshop.enums.StateUser;
import com.datn.laptopshop.repos.RoleRepository;
import com.datn.laptopshop.repos.TokenRepository;
import com.datn.laptopshop.repos.UserRepository;
import com.datn.laptopshop.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@Transactional
public class UserService implements IUserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTService jwtService;


    @Override
    public ResponseEntity<Object> login(SignInRequest u) throws Exception{
        var user = userRepository.findUserByEmail(u.getEmail());
        Map m = new HashMap<>();
        if(user.isPresent()){
            if (passwordEncoder.matches(u.getPassword(), user.get().getPassword())){
                var jwtToken = jwtService.generateToken(user.get());
                var refreshToken = jwtService.generateRefreshToken(user.get());
                m.put("accessToken", jwtToken);
                m.put("refreshToken", refreshToken);
                m.put("role", user.get().getRole().getName());
                revokeAllUserTokens(user.get());
                saveUserToken(user.get(), jwtToken);
                return ResponseHandler.responseBuilder("success",
                        "Login success",
                        HttpStatus.OK,m,0);
            }
        }
        return ResponseHandler.responseBuilder("error","login failed !!!",
                HttpStatus.BAD_REQUEST,"",99);
    }

    @Override
    public UserDto register(SignUpRequest u,String token, long tokenExpireAt) throws Exception{
        try{
            Optional<User> userActive = userRepository.findByEmailAndStateUserAndAuthType(u.getEmail(), StateUser.ACTIVED, AuthenticationType.DATABASE);
            if (!userActive.isEmpty()) {
                throw new Exception("The email already exists!");
            }
            Optional<User> userPending = userRepository.findByEmailAndStateUserAndAuthType(u.getEmail(), StateUser.PENDING, AuthenticationType.DATABASE);
            if(!userPending.isEmpty()) {
                UserDto userDto = new UserDto().toUserDTO(userPending.get());
                return userDto;
            }
            User user = new User();
            user.setFullname(u.getFullname());
            user.setHash_pw(passwordEncoder.encode(u.getPassword()));
            user.setEmail(u.getEmail());
            user.setStateUser(StateUser.PENDING);
            user.setAuthType(AuthenticationType.DATABASE);
            user.setCreated_at(new Date());
            Role role = roleRepository.findById((long) 2).get();
            user.setRole(role);
            user.setRegisterToken(token);
            user.setRegisterTokenExpireAt(tokenExpireAt);

            User userSave = userRepository.save(user);
            if(!userRepository.existsById(userSave.getId())) {
                return null;
            }
            UserDto userDto = new UserDto().toUserDTO(user);
            return userDto;
        }
        catch (Exception e){
            System.out.println("Err: "+e);
            return null;
        }
    }

    @Override
    public boolean checkRegisterToken(String token) throws Exception{
        Optional<User> user = userRepository.findByRegisterToken(token);
        if(user.isPresent()) {
            long tokenExpiredAt = user.get().getRegisterTokenExpireAt();
            long currentTime = new Date().getTime();

            if(tokenExpiredAt - currentTime > 0) {
                user.get().setStateUser(StateUser.ACTIVED);
                userRepository.save(user.get());
                return true;
            }else {
                if(user.get().getStateUser().equals(StateUser.PENDING)) {
                    userRepository.delete(user.get());
                }
                throw new Exception("Your link is expired!");
            }
        }
        return false;
    }

    @Override
    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .accessToken(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    @Override
    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if(validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        System.out.println("da vao refresh 1 !! userEmail: "+ userEmail);
        if(userEmail != null){
            System.out.println("da vao refresh 2");
            var user = userRepository.findUserByEmail(userEmail).orElseThrow();
            System.out.println("user: "+user);
            if(jwtService.isTokenValid(refreshToken, user)){
                System.out.println("da vao refresh 3");
                var accessToken = jwtService.generateToken(user);
                System.out.println("da vao refresh 4");
                revokeAllUserTokens(user);
                System.out.println("da vao refresh 5");
                saveUserToken(user, accessToken);
                System.out.println("da vao refresh 6");
                Map m = new HashMap<>();
                m.put("accessToken", jwtService.generateToken(user));
                m.put("refreshToken", jwtService.generateRefreshToken(user));
                System.out.println("refresh Token *****");
                new ObjectMapper().writeValue(response.getOutputStream(), m);
            }
        }
    }

    @Override
    public UserDto findUserByEmail(String name) {
         var u = userRepository.findUserByEmail(name);
         if (u.isPresent()){
             return new UserDto().toUserDTO(u.get());
         }
         return null;
    }

    @Override
    public User findUserByEmailGG(String name) {
        var u = userRepository.findUserByEmail(name);
        if (u.isPresent()){
            return u.get();
        }
        return null;
    }

    @Override
    public UserDto findbyId(long id) {
        var u = userRepository.findById(id);
        if (u.isPresent())
            return new UserDto().toUserDTO(u.get());
        return null;
    }

    @Override
    public boolean update(EditProfileRequest profile) {
        if (profile == null)
            return false;

        var u = userRepository.findById(profile.getId());
        if (u.isPresent())
        {
            u.get().setId(profile.getId());
            if (profile.getFullname() != null)
            u.get().setFullname(profile.getFullname());
            if (profile.getAddress() != null)
                u.get().setAddress(profile.getAddress());
            if (profile.getSex() != null)
                u.get().setGender(profile.getSex());
            if (profile.getBirthday() != null)
                u.get().setDob(profile.getBirthday());
            if (profile.getImg() != null)
            u.get().setImg(profile.getImg());
            u.get().setUpdate_at(new Date());
            userRepository.save(u.get());
        }

        return true;
    }

    @Override
    public boolean changePW(long id, String oldPW, String newPW) {
        if (oldPW == null || newPW == null || oldPW=="" || newPW=="" || oldPW == newPW)
            return false;
        var u = userRepository.findById(id);
        if (u.isPresent()){
            if (passwordEncoder.matches(oldPW, u.get().getPassword())){
                u.get().setHash_pw(passwordEncoder.encode(newPW));
                userRepository.save(u.get());
                return true;
            }
        }

        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found"));
        var authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
        if (user.getPassword() != null && user.getPassword() != "")
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),authorities);
        else {
            return new org.springframework.security.core.userdetails.User(user.getEmail(), "0906088493",authorities);
        }
    }

}
