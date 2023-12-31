package com.datn.laptopshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileRequest {
    private int id;
    private String fullname;
    private String address;
    private String img;
    private String sex;
    private String birthday;
    private String email;
    private String phone;

    @Override
    public String toString() {
        return "EditProfileRequest{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", address='" + address + '\'' +
                ", img='" + img + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
