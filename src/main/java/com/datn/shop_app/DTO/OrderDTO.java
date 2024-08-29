package com.datn.shop_app.DTO;

import com.datn.shop_app.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Integer userId;

    @Size(max = 30, message = "Max length of full name is 30 characters")
    @NotNull(message = "Full name cannot be null")
    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    @Size(max = 10, message = "Max length of phone number is 10 characters")
    @NotNull(message = "Phone number cannot be null")
    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    @Size(max = 100, message = "Max length of email is 100 characters")
    @Pattern(regexp = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", message = "{Pattern.user.email}")
    private String email;

    @Size(max = 255, message = "Max length of address is 255 characters")
    private String address;

    @Size(max = 255, message = "Max length of address is 255 characters")
    private String note;

    @NotNull(message = "Payment method cannot be null")
    @NotBlank(message = "Payment method cannot be blank")
    private String paymentMethod;

    @NotNull(message = "Status cannot be null")
    @NotBlank(message = "Status cannot be blank")
    private String status;

    private List<CartItemDTO> cartItems;
}
