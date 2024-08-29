package com.datn.shop_app.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommodityDTO {
    @NotBlank(message = "Commodity name cannot be blank")
    @NotNull(message = "Commodity name cannot be null")
    private String commodityName;
}
