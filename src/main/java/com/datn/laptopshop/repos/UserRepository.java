package com.datn.laptopshop.repos;


import com.datn.laptopshop.entity.User;
import com.datn.laptopshop.enums.AuthenticationType;
import com.datn.laptopshop.enums.StateUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email = ?1")
    Optional<User> findUserByEmail(String email);

    @Query("select u from User u where u.email = ?1 and u.stateUser = ?2 and u.authType = ?3")
    Optional<User> findByEmailAndStateUserAndAuthType(String email, StateUser actived, AuthenticationType authType);

    @Query("select u from User u where u.registerToken = ?1")
    Optional<User> findByRegisterToken(String token);
}
