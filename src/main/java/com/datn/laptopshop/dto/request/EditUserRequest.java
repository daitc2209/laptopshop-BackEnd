package com.datn.laptopshop.dto.request;

import com.datn.laptopshop.enums.AuthenticationType;
import com.datn.laptopshop.enums.StateUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditUserRequest {
    private Long id;

    private String fullname;

    private String sex;

    private String birthday;

    private String email;

    private String address;

    private StateUser stateUser;

    private AuthenticationType authType;

    private Long role;

    private String img;

    private String phone;

    public boolean isEmpty()  {
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.get(this)!=null) {
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Exception occured in processing");
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "EditUserRequest{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", address='" + address + '\'' +
                ", stateUser=" + stateUser +
                ", authType=" + authType +
                ", role=" + role +
                ", img='" + img + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
