package com.datn.shop_app.service.impl;

import com.datn.shop_app.entity.Role;
import com.datn.shop_app.repository.RoleRepository;
import com.datn.shop_app.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAllByActive(true);
    }

}
