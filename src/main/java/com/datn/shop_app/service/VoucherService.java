package com.datn.shop_app.service;

import com.datn.shop_app.DTO.VoucherDTO;
import com.datn.shop_app.entity.Promotion;
import com.datn.shop_app.entity.Voucher;
import com.datn.shop_app.response.voucher.VoucherResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

public interface VoucherService {
    Page<VoucherResponse> getAllVouchers(String code, LocalDate startDate, LocalDate endDate, Boolean active, Pageable pageable);

    Voucher save(VoucherDTO voucherDTO);

    List<String> validate(VoucherDTO voucherDTO, BindingResult bindingResult, boolean isCreate);

    Voucher update(Integer id, VoucherDTO voucherDTO);

    Voucher getVoucher(Integer id);

    void delete(Integer id);

    List<Voucher> getVouchers(LocalDate endDate);

    void setActive(List<Voucher> vouchers);

    Voucher getVoucherByCode(String code);
}
