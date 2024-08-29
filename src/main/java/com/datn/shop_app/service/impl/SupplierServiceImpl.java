package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.SupplierDTO;
import com.datn.shop_app.entity.Supplier;
import com.datn.shop_app.repository.SupplierRepository;
import com.datn.shop_app.response.supplier.SupplierResponse;
import com.datn.shop_app.service.SupplierService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public Supplier save(SupplierDTO supplierDTO) {
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierDTO, supplier);
        supplier.setActive(true);
        return supplierRepository.save(supplier);
    }

    @Override
    public List<String> validateSupplier(BindingResult result, SupplierDTO supplierDTO) {
        List<String> errors = new ArrayList<>();
        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }

            return errors;
        }

        Optional<Supplier> supplier = supplierRepository.findByPhoneNumberAndActive(supplierDTO.getPhoneNumber(), true);
        if (supplier.isPresent()) {
            errors.add("Phone number already in use");
        }

        return errors;
    }

    @Override
    public Page<SupplierResponse> getAllSuppliers(String name, String phoneNumber, String email, Boolean active, Pageable pageable) {
        Page<Supplier> supplierPage = supplierRepository.findAllSuppliers(name, phoneNumber, email, active, pageable);
        return supplierPage.map(SupplierResponse::fromSupplier);
    }

    @Override
    public List<SupplierResponse> getAllSuppliers(String name, Boolean active) {
        List<Supplier> suppliers = supplierRepository.findAllSuppliers(name, active);
        List<SupplierResponse> supplierResponses = new ArrayList<>();
        for (Supplier supplier : suppliers) {
            supplierResponses.add(SupplierResponse.fromSupplier(supplier));
        }
        return supplierResponses;
    }

    @Override
    public Supplier getSupplier(Integer id) {
        Supplier supplier = supplierRepository.findById(id).orElse(null);
        return supplier;
    }

    @Override
    @Transactional
    public Supplier updateSupplier(Integer id, SupplierDTO supplierDTO) {
        Supplier supplier = supplierRepository.findById(id).orElse(null);
        BeanUtils.copyProperties(supplierDTO, supplier);
        return supplierRepository.save(supplier);
    }

    @Override
    public List<String> validateUpdateSupplier(BindingResult result, SupplierDTO supplierDTO, Supplier supplier) {
        List<String> errors = new ArrayList<>();
        if(supplier == null) {
            errors.add("Supplier is not exist");
            return errors;
        }

        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }

            return errors;
        }

        Optional<Supplier> existSupplier = supplierRepository.findByPhoneNumberAndActive(supplierDTO.getPhoneNumber(), true);
        if (!supplierDTO.getPhoneNumber().equals(supplier.getPhoneNumber())
            & existSupplier.isPresent()) {
            errors.add("Phone number already in use");
        }
        return errors;
    }

    @Override
    public void deleteSupplier(Integer id) {
        Optional<Supplier> supplier = supplierRepository.findByIdAndActive(id, true);
        if(supplier.isPresent()) {
            supplier.get().setActive(false);
            supplierRepository.save(supplier.get());
        }
    }

}
