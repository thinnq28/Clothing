package com.datn.shop_app.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateVariantDTO {
    @NotNull(message = "Quantity is not null")
    @Min(value = 0, message = "Quantity must be greater than 0")
    private Integer quantity;

    @NotNull(message = "Price is not null")
    @Min(value = 0, message = "Price must be greater than 0")
    private Float price;

    @NotNull(message = "Product is not null")
    private Integer productId;

    @NotEmpty(message = "Property of variant is not empty")
    private List<Integer> properties;

    private List<Integer> imageIds;
}
