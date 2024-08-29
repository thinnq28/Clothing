package com.datn.shop_app.model;

import lombok.Data;

@Data
public class PurchaseOrderModel {
    private Integer ordinalNumber;
    private String skuId;
    private Integer quantity;
    private Double unitPrice;
    private Double totalAmount;
}
