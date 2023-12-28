package com.datn.laptopshop.dto.request;

import com.datn.laptopshop.enums.AuthenticationType;
import com.datn.laptopshop.enums.StateUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserRequest {
    private String fullname;
    private String sex;
    private String address;
    private String email;
    private StateUser stateUser;
    private int role;
    private AuthenticationType authType;

    @Override
    public String toString() {
        return "SearchUserRequest{" +
                "fullname='" + fullname + '\'' +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", stateUser=" + stateUser +
                ", authType=" + authType +
                '}';
    }
}
