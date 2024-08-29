package com.datn.shop_app.repository;

import com.datn.shop_app.entity.Role;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    UserRole save(UserRole userRole);

    void deleteById(int id);

    Optional<UserRole> findByUserAndRole(User user, Role role);

    @Query("SELECT u FROM UserRole u " +
            "WHERE u.role.id = :roleId AND u.user.id = :userId")
    List<UserRole> findByUserAndRole(Integer userId, Integer roleId);
}