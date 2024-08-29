package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.PurchaseOrderDTO;
import com.datn.shop_app.entity.PurchaseOrder;
import com.datn.shop_app.model.PurchaseOrderModel;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.purchase_order.ListPurchaseOrderResponse;
import com.datn.shop_app.response.purchase_order.PurchaseOrderResponse;
import com.datn.shop_app.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/purchase-orders")
public class PurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CREATE')")
    public ResponseEntity<ResponseObject> upload(@ModelAttribute("file") MultipartFile file) {
        try {
            //kiểm tra kích thước file và định dạng
            if (file.getSize() > 50 * 1024 * 1024) {// > 50MB
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
                        ResponseObject.builder().message("File is too large! Maximum size is 50MB")
                                .status(HttpStatus.BAD_REQUEST).build());
            }

            List<PurchaseOrderModel> purchaseOrders = purchaseOrderService.readExcelFileToProducts(file);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                    .message("Read file excel is successful")
                    .data(purchaseOrders)
                    .status(HttpStatus.OK).build());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                    .message("Read file excel is failed")
                    .status(HttpStatus.BAD_REQUEST).build());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CREATE')")
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody PurchaseOrderDTO purchaseOrderDTO,
                                                 BindingResult bindingResult) {
       try {
           List<String> errors = purchaseOrderService.validateInsertion(purchaseOrderDTO, bindingResult);
           if (!errors.isEmpty()) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                       .message("Create Purchase Order is failed")
                       .data(errors)
                       .status(HttpStatus.BAD_REQUEST).build());
           }

           PurchaseOrder purchaseOrder = purchaseOrderService.save(purchaseOrderDTO);
           if(purchaseOrder == null) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                       .message("Create Purchase Order is failed")
                       .status(HttpStatus.BAD_REQUEST).build());
           }

           return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                   .message("Create Purchase Order is successful")
                   .data(purchaseOrder)
                   .status(HttpStatus.OK).build());
       }catch (Exception e) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                   .message("Create Purchase Order is failed")
                   .status(HttpStatus.BAD_REQUEST).build());
       }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_READ')")
    public ResponseEntity<ResponseObject> getAllVouchers(
                                                         @RequestParam(defaultValue = "") LocalDate orderDate,
                                                         @RequestParam(defaultValue = "") String supplierName,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<PurchaseOrderResponse> orderPage = purchaseOrderService.getPurchaseOrders(supplierName, orderDate, pageRequest);
        totalPages = orderPage.getTotalPages();
        List<PurchaseOrderResponse> orderResponses = orderPage.getContent();

        ListPurchaseOrderResponse listPurchaseOrderResponse = ListPurchaseOrderResponse.builder()
                .orders(orderResponses)
                .totalPages(totalPages).build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get purchase orders is successful")
                .status(HttpStatus.OK)
                .data(listPurchaseOrderResponse)
                .build());
    }

}
