package com.datn.laptopshop.dto;

import com.datn.laptopshop.entity.User;
import com.datn.laptopshop.enums.AuthenticationType;
import com.datn.laptopshop.enums.StateUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String fullname;
    private String email;
    private String phone;
    private String gender;
    private String dob;
    private String address;
    private StateUser stateUser;
    private AuthenticationType authType;
    private String role;
    private String img;

    public UserDto toUserDTO(User user) {
        return new UserDto(user.getId(), user.getUsername(),user.getFullname(), user.getEmail(), user.getPhone(),
                user.getGender(), user.getDob(), user.getAddress(), user.getStateUser(), user.getAuthType(),
                user.getRole().getName(), user.getImg());
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", dob='" + dob + '\'' +
                ", address='" + address + '\'' +
                ", stateUser=" + stateUser +
                ", authType=" + authType +
                ", role='" + role + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
