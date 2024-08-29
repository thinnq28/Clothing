package com.datn.shop_app.controller.admin;

import com.datn.shop_app.entity.Role;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.role.RoleResponse;
import com.datn.shop_app.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_READ')")
    public ResponseEntity<ResponseObject> getAllRoles() {
        List<Role> roles = roleService.findAll();
        List<RoleResponse> roleResponses = new ArrayList<>();
        for (Role role : roles) {
            RoleResponse roleResponse = RoleResponse.fromRole(role);
            roleResponses.add(roleResponse);
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get all role is successful")
                .status(HttpStatus.OK)
                .data(roleResponses).build());
    }

}
