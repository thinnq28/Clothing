package com.datn.shop_app.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionVariantDTO {
    @NotNull(message = "Variant cannot be null")
    private int variantId;
    @NotNull(message = "Promotion cannot be null")
    private int promotionId;
}
