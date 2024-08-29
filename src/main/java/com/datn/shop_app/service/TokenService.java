package com.datn.shop_app.service;

import com.datn.shop_app.entity.Token;
import com.datn.shop_app.entity.User;
import jakarta.transaction.Transactional;

public interface TokenService {
    @Transactional
    Token refreshToken(String refreshToken, User user) throws Exception;

    @Transactional
    Token addToken(User user, String token, boolean isMobileDevice);
}
