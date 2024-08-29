package com.datn.shop_app.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserRoleDTO {
    @NotNull(message = "Role cannot be null")
    private Integer roleId;

    @NotNull(message = "User cannot be null")
    private Integer userId;
}
