package com.datn.shop_app.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusOrderDTO {
    @NotBlank(message = "Status is not blank")
    @NotBlank(message = "Status is not null")
    private String status;
}
