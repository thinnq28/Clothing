package com.datn.shop_app.response.role;

import com.datn.shop_app.entity.Role;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.entity.UserRole;
import com.datn.shop_app.response.user.UserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {
    private Integer id;

    @JsonProperty("name")
    private String roleName;

    private List<UserRole> userRoles;

    public static RoleResponse fromRole(Role role) {
        RoleResponse roleResponse = new RoleResponse();
        BeanUtils.copyProperties(role, roleResponse);

        return roleResponse;
    }
}
