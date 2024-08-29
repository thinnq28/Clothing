package com.datn.shop_app.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InsertVariantDTO {

    private Integer quantity;

    @NotNull(message = "Price is not null")
    @Min(value = 0, message = "Price must be greater than 0")
    private Float price;

    @NotNull(message = "Product is not null")
    private Integer productId;

    List<OptionVariantDTO> options;
}
