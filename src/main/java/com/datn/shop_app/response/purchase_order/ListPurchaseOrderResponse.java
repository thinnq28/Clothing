package com.datn.shop_app.response.purchase_order;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListPurchaseOrderResponse {
    private List<PurchaseOrderResponse> orders;
    private Integer totalPages;
}
