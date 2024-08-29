package com.datn.shop_app.service;

import com.datn.shop_app.DTO.PurchaseOrderDTO;
import com.datn.shop_app.entity.PurchaseOrder;
import com.datn.shop_app.model.PurchaseOrderModel;
import com.datn.shop_app.response.purchase_order.PurchaseOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderService {
    List<PurchaseOrderModel> readExcelFileToProducts(MultipartFile file) throws IOException;

    PurchaseOrder save(PurchaseOrderDTO purchaseOrderDTO);

    List<String> validateInsertion(PurchaseOrderDTO purchaseOrderDTO, BindingResult bindingResult);

    Page<PurchaseOrderResponse> getPurchaseOrders(String supplierName, LocalDate orderDate, Pageable pageable);
}
