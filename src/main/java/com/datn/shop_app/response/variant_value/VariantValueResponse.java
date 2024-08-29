package com.datn.shop_app.response.variant_value;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariantValueResponse {
    private Integer id;

    private Integer variantId;

    private Integer optionValueId;
}
