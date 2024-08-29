package com.datn.shop_app.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuantityVariantDTO {
    @NotNull(message = "Quantity is not null")
    @Min(value = 0, message = "Quantity must be greater than 0")
    private Integer quantity;

    @NotNull(message = "Sku ID cannot be null")
    @NotBlank(message = "Sku ID cannot be blank")
    private String SkuId;
}
