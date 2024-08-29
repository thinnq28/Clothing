package com.datn.shop_app.repository;

import com.datn.shop_app.entity.Token;
import com.datn.shop_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Token findByToken(String token);
    Token findByRefreshToken(String refreshToken);
    List<Token> findByUser(User user);
}