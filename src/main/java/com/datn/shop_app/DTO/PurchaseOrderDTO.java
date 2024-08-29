package com.datn.shop_app.DTO;

import com.datn.shop_app.model.PurchaseOrderModel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDTO {
    @NotNull(message = "Supplier cannot be null")
    private Integer supplierId;

    @NotNull(message = "Total amount cannot be null")
    @Min(value = 0, message = "Total amount must greater than 0")
    private BigDecimal totalAmount;

    private List<PurchaseOrderModel> purchaseOrderModels;
}
