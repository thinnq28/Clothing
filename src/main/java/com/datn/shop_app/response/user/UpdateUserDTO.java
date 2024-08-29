package com.datn.shop_app.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserDTO {
    @JsonProperty("fullname")
    @NotNull(message = "Full name cannot be null")
    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    private String address;

    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

}