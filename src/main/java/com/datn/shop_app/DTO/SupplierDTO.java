package com.datn.shop_app.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SupplierDTO {

    @NotBlank(message = "Supplier name cannot be blank")
    @NotNull(message = "Supplier name cannot be null")
    private String supplierName;

    @NotNull(message = "Phone number cannot be null")
    @NotBlank(message = "Phone number cannot be blank")
    @Length(max = 10, min = 10, message = "Phone number must be 10 digits")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("email")
    @Pattern(regexp = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", message = "Pattern of email is not correct")
    private String email;
}
