package com.datn.shop_app.response.purchase_order;

import com.datn.shop_app.entity.PurchaseOrder;
import com.datn.shop_app.entity.Variant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderDetailResponse {
    private Integer id;

    private String variantName;

    private Integer quantity;

    private Double unitPrice;

    private Double totalPrice;
}
