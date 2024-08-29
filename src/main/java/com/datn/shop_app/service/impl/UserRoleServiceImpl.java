package com.datn.shop_app.service.impl;

import com.datn.shop_app.entity.Role;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.entity.UserRole;
import com.datn.shop_app.exception.DataNotFoundException;
import com.datn.shop_app.repository.RoleRepository;
import com.datn.shop_app.repository.UserRepository;
import com.datn.shop_app.repository.UserRoleRepository;
import com.datn.shop_app.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserRole save(Integer userId, Integer roleId) throws DataNotFoundException {
        Optional<User> user = userRepository.findByIdAndActive(userId, true);
        Optional<Role> role = roleRepository.findByIdAndActive(roleId, true);
        if(user.isEmpty() || role.isEmpty()) {
            throw new DataNotFoundException("User or Role is not exist");
        }

        UserRole userRole = new UserRole();
        userRole.setUser(user.get());
        userRole.setRole(role.get());

        return userRoleRepository.save(userRole);
    }

    @Override
    public void delete(Integer userId, Integer roleId) throws DataNotFoundException {
        Optional<User> user = userRepository.findByIdAndActive(userId, true);
        Optional<Role> role = roleRepository.findByIdAndActive(roleId, true);
        if(user.isEmpty() || role.isEmpty()) {
            throw new DataNotFoundException("User or Role is not exist");
        }

        List<UserRole> userRoles = userRoleRepository.findByUserAndRole(userId, roleId);
        if(userRoles.isEmpty()) {
            throw new DataNotFoundException("UserROle is not exist");
        }

        userRoleRepository.deleteAll(userRoles);
    }
}
