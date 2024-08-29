package com.datn.shop_app.service;

import com.datn.shop_app.entity.UserRole;
import com.datn.shop_app.exception.DataNotFoundException;

public interface UserRoleService {
    UserRole save(Integer userId, Integer roleId) throws DataNotFoundException;

    void delete(Integer userId, Integer roleId) throws DataNotFoundException;
}
