package com.datn.shop_app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLoginDTO {
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number cannot be blank")
    @NotNull(message = "Phone number cannot be null")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @NotNull(message = "Password cannot be null")
    private String password;

    public boolean isPasswordBlank() {
        return password == null || password.trim().isEmpty();
    }

}
