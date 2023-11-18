package com.datn.laptopshop.repos;

import com.datn.laptopshop.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("select t from Token t where t.accessToken = ?1")
    Optional<Token> findByToken(String token);

    @Query("select t from Token t inner join User u" +
            " on t.user.id = u.id where u.id = ?1 and (t.expired=false or t.revoked=false)")
    List<Token> findAllValidTokenByUser(Long id);

    @Query("select t from Token t where t.user.id = ?1")
    Optional<Token> findByUserId(long id);
}
