package com.datn.shop_app.response.purchase_order;

import com.datn.shop_app.entity.PurchaseOrder;
import com.datn.shop_app.entity.PurchaseOrderDetail;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderResponse {
    private Integer id;

    private LocalDate orderDate;

    private String supplierName;

    private BigDecimal totalAmount;

    private List<PurchaseOrderDetailResponse> details;

    public static PurchaseOrderResponse fromPurchaseOrder(PurchaseOrder purchaseOrder) {
        PurchaseOrderResponse purchaseOrderResponse = new PurchaseOrderResponse();
        BeanUtils.copyProperties(purchaseOrder, purchaseOrderResponse);
        purchaseOrderResponse.setSupplierName(purchaseOrder.getSupplier().getSupplierName());

        List<PurchaseOrderDetailResponse> details = new ArrayList<>();

        List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrder.getPurchaseOrderDetails();

        if(purchaseOrderDetails != null && !purchaseOrderDetails.isEmpty()) {
            for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails) {
                PurchaseOrderDetailResponse purchaseOrderDetailResponse = new PurchaseOrderDetailResponse();
                BeanUtils.copyProperties(purchaseOrderDetail, purchaseOrderDetailResponse);
                purchaseOrderDetailResponse.setVariantName(purchaseOrderDetail.getVariant().getVariantName());
                details.add(purchaseOrderDetailResponse);
            }

        }
        purchaseOrderResponse.setDetails(details);
        return purchaseOrderResponse;
    }
}
