package com.datn.shop_app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO {

//    @NotBlank(message = "Old password is not blank")
//    @NotNull(message = "Old password is not null")
//    @JsonProperty("old-password")
//    private String odlPassword;

    @NotBlank(message = "New password is not blank")
    @NotNull(message = "New password is not null")
    @JsonProperty("new_password")
    private String newPassword;

    @NotBlank(message = "Retype password is not blank")
    @NotNull(message = "Retype password is not null")
    @JsonProperty("confirm_password")
    private String confirmPassword;
}
