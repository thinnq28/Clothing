package com.datn.shop_app.service;

import com.datn.shop_app.DTO.SupplierDTO;
import com.datn.shop_app.entity.Supplier;
import com.datn.shop_app.response.supplier.SupplierResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface SupplierService {
    Supplier save(SupplierDTO supplierDTO);

    List<String> validateSupplier(BindingResult result, SupplierDTO supplierDTO);

    Page<SupplierResponse> getAllSuppliers(String name, String phoneNumber, String email, Boolean active, Pageable pageable);

    List<SupplierResponse> getAllSuppliers(String name, Boolean active);

    Supplier getSupplier(Integer id);

    @Transactional
    Supplier updateSupplier(Integer id, SupplierDTO supplierDTO);

    List<String> validateUpdateSupplier(BindingResult result, SupplierDTO supplierDTO, Supplier supplier);

    void deleteSupplier(Integer id);
}
