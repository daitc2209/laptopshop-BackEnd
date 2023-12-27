package com.datn.laptopshop.entity;

import com.datn.laptopshop.enums.AuthenticationType;
import com.datn.laptopshop.enums.StateUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", length = 50,unique = true)
    private String username;

    @Column(name = "hash_pw", nullable = false)
    private String hash_pw;

    @Column(name = "fullname", length = 50)
    private String fullname;

    @Column(name = "email", length = 50, unique = true)
    private String email;

    @Column(name = "gender", length = 12)
    private String gender;

    @Column(name = "dob")
    private String dob;

    @Column(name = "address")
    private String address;

    @Column(name = "img")
    private String img;

    @Column(name = "phone", length = 11)
    private String phone;

    @Column(name = "register_token")
    private String registerToken;

    @Column(name = "register_token_expire_at")
    private Long registerTokenExpireAt;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expire_at")
    private Long resetPasswordTokenExpireAt;

    @Column(name = "created_at", length = 20)
    private Date created_at;

    @Column(name = "updated_at", length = 20)
    private Date update_at;

    @Enumerated(EnumType.STRING)
    @Column(name = "stateUser", length = 20)
    private StateUser stateUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", length = 20)
    private AuthenticationType authType;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Favourite> favourites;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CheckOut> checkOuts;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(this.role.getName()));
        return auth;
    }

    @Override
    public String getPassword() {
        return hash_pw;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
