package com.datn.shop_app.response.user;

import com.datn.shop_app.entity.Role;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.entity.UserRole;
import com.datn.shop_app.response.role.RoleResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@Builder
public class UserResponse {

    private Integer id;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber = "";
    @JsonProperty("email")

    private String email = "";

    private String address = "";

    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("is_active")
    private Boolean active;

    @JsonProperty("roles")
    //role admin not permitted
    private List<RoleResponse> roles;

    private int totalPages;

    public static UserResponse fromUser(User user) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        List<RoleResponse> roles = new ArrayList<>();
        for (UserRole userRole : user.getUserRoles()) {
            RoleResponse response = RoleResponse.fromRole(userRole.getRole());
            roles.add(response);
        }

        userResponse.setRoles(roles);
        return userResponse;
    }
}
