package com.datn.laptopshop.repos;


import com.datn.laptopshop.dto.request.SearchUserRequest;
import com.datn.laptopshop.entity.User;
import com.datn.laptopshop.enums.AuthenticationType;
import com.datn.laptopshop.enums.StateUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.username = ?1")
    Optional<User> findUserByUsername(String username);

    @Query("select u from User u where u.email = ?1 ")
    Optional<User> findUserByEmail(String email);

    @Query("select u from User u where (?1 is null or ?1 = '' or u.username = ?1)" +
            " and u.email = ?2 and u.stateUser = ?3 and u.authType = ?4")
    Optional<User> findByEmailAndStateUserAndAuthType(String username ,String email, StateUser actived, AuthenticationType authType);

    @Query("select u from User u where u.registerToken = ?1")
    Optional<User> findByRegisterToken(String token);

    @Query("select u from User u where " +
            "(?1 is null or ?1 = '' or u.fullname like %?1%) " +
            "and (?2 is null or ?2 = '' or u.gender like %?2%) " +
            "and (?3 is null or ?3 = '' or u.address like %?3%) " +
            "and (?4 is null or ?4 = '' or u.email like %?4%) " +
            "and (?5 is null or u.stateUser = ?5) " +
            "and (?6 is null or u.authType = ?6) " +
            "and (?7 = 0 or u.role.id = ?7)")
    Page<User> findAll(String fullname,
                       String sex,
                       String address,
                       String email,
                       StateUser stateUser,
                       AuthenticationType authType,
                       Long role,
                       Pageable pageable);
}
