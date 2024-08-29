package com.datn.shop_app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    @JsonProperty("fullname")
    @NotNull(message = "{NotNull.user.fullName}")
    @NotBlank(message = "{NotBlank.user.fullName}")
    private String fullName;

    @JsonProperty("phone_number")
    @NotNull(message = "{NotNull.user.phoneNumber}")
    @NotBlank(message = "{NotBlank.user.phoneNumber}")
    private String phoneNumber = "";


    @JsonProperty("email")
    @NotNull(message = "{NotNull.user.email}")
    @NotBlank(message = "{NotBlank.user.email}")
    @Pattern(regexp = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", message = "{Pattern.user.email}")
    private String email = "";

    private String address = "";

    @NotBlank(message = "{NotBlank.user.password}")
    @NotNull(message = "{NotNull.user.password}")
    private String password = "";

    @JsonProperty("retype_password")
    @NotBlank(message = "{NotBlank.user.retypePassword}")
    @NotNull(message = "{NotNull.user.retypePassword}")
    private String retypePassword = "";

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @NotNull(message = "Role ID is required")
    @JsonProperty("role_id")
    //role admin not permitted
    private Integer roleId;


}
