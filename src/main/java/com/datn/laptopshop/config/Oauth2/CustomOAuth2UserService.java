package com.datn.laptopshop.config.Oauth2;

import com.datn.laptopshop.enums.AuthenticationType;
import com.datn.laptopshop.enums.StateUser;
import com.datn.laptopshop.repos.RoleRepository;
import com.datn.laptopshop.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.datn.laptopshop.entity.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        String oauth2ClientName = userRequest.getClientRegistration().getClientName();
        OAuth2User user = super.loadUser(userRequest);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(user);

        AuthenticationType authType = AuthenticationType.valueOf(oauth2ClientName.toUpperCase());
        Optional<User> u = userRepository.findByEmailAndStateUserAndAuthType(customOAuth2User.getEmail(), StateUser.ACTIVED, authType);
        if (!u.isPresent()) {
            customOAuth2User.setId(registerOauth2(customOAuth2User, authType));
        } else {
            customOAuth2User.setId(u.get().getId());
        }

        return customOAuth2User;
    }

    private long registerOauth2(CustomOAuth2User customOAuth2User, AuthenticationType authType) {

        User user = new User();
        user.setEmail(customOAuth2User.getEmail());
        user.setFullname(customOAuth2User.getName());
        user.setAddress(" ");
        user.setImg(customOAuth2User.getImg());
        user.setRole(roleRepository.findById((long)2).get());
        user.setCreated_at(new Date());
        user.setStateUser(StateUser.ACTIVED);
        user.setAuthType(authType);

        return userRepository.save(user).getId();
    }

}
