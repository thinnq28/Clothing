package com.datn.shop_app.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartItemDTO {
    private Integer variantId;

    private Integer quantity;
}
