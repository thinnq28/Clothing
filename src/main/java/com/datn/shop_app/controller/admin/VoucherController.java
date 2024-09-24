package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.VoucherDTO;
import com.datn.shop_app.entity.Order;
import com.datn.shop_app.entity.Promotion;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.entity.Voucher;
import com.datn.shop_app.repository.OrderRepository;
import com.datn.shop_app.repository.UserRepository;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.promotion.PromotionResponse;
import com.datn.shop_app.response.voucher.ListVoucherResponse;
import com.datn.shop_app.response.voucher.VoucherResponse;
import com.datn.shop_app.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/vouchers")
public class VoucherController {
    private final VoucherService voucherService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_READ')")
    public ResponseEntity<ResponseObject> getAllVouchers(@RequestParam(defaultValue = "") String code,
                                                         @RequestParam(defaultValue = "") LocalDate startDate,
                                                         @RequestParam(defaultValue = "") LocalDate endDate,
                                                         @RequestParam(defaultValue = "true", name = "active") Boolean active,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<VoucherResponse> voucherPage = voucherService.getAllVouchers(code, startDate, endDate, active, pageRequest);
        totalPages = voucherPage.getTotalPages();
        List<VoucherResponse> voucherResponses = voucherPage.getContent();

        ListVoucherResponse listPromotionResponse = ListVoucherResponse.builder()
                .vouchers(voucherResponses)
                .totalPages(totalPages).build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get voucher is successful")
                .status(HttpStatus.OK)
                .data(listPromotionResponse)
                .build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CREATE')")
    public ResponseEntity<ResponseObject> createVoucher(@Valid @RequestBody VoucherDTO voucherDTO, BindingResult bindingResult) {

        try {
            List<String> errors = voucherService.validate(voucherDTO, bindingResult, true);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Create voucher is not successful")
                        .status(HttpStatus.BAD_REQUEST)
                        .data(errors).build());
            }

            Voucher voucher = voucherService.save(voucherDTO);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Create voucher is successful")
                    .status(HttpStatus.OK)
                    .data(VoucherResponse.fromVoucher(voucher))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Create voucher is not successful")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_UPDATE')")
    public ResponseEntity<ResponseObject> updateVoucher(@PathVariable Integer id,
                                                        @Valid @RequestBody VoucherDTO voucherDTO, BindingResult bindingResult) {
        try {
            Voucher voucher = voucherService.getVoucher(id);
            if (voucher == null) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Voucher is not found")
                        .status(HttpStatus.BAD_REQUEST)
                        .build());
            }

            List<String> errors = voucherService.validate(voucherDTO, bindingResult, false);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Update voucher is not successful")
                        .status(HttpStatus.BAD_REQUEST)
                        .data(errors).build());
            }

            voucher = voucherService.update(id, voucherDTO);

            if (voucher == null) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Update voucher is not successful")
                        .status(HttpStatus.BAD_REQUEST)
                        .build());
            }

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Update voucher is successful")
                    .status(HttpStatus.OK)
                    .data(VoucherResponse.fromVoucher(voucher))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Update voucher is not successful")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_READ')")
    public ResponseEntity<ResponseObject> getVoucherDetail(@PathVariable Integer id) {
        Voucher voucher = voucherService.getVoucher(id);

        if (voucher == null) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Voucher is not exist")
                    .status(HttpStatus.NOT_FOUND)
                    .data("").build());
        }

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get Voucher is successful")
                .status(HttpStatus.OK)
                .data(VoucherResponse.fromVoucher(voucher)).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELETE')")
    public ResponseEntity<ResponseObject> deleteSupplier(@PathVariable Integer id) {
        Voucher voucher = voucherService.getVoucher(id);
        if (voucher == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Voucher is not exist")
                    .status(HttpStatus.NOT_FOUND).build());
        }

        voucherService.delete(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Delete promotion is successful")
                .status(HttpStatus.OK)
                .data("").build());
    }

    @GetMapping("/by-code")
    public ResponseEntity<ResponseObject> getVoucherByVoucherCode(@RequestParam String code,
                                                                  @RequestParam Integer userId) {
        List<String> errors = new ArrayList<>();
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Voucher code is required")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }

        Voucher voucher = voucherService.getVoucherByCode(code);

        if (voucher == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Voucher is not exist")
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .build());
        }

        if (LocalDate.now().isBefore(voucher.getStartDate())) {
            errors.add("Voucher has not been applied yet");
        }

        if (LocalDate.now().isAfter(voucher.getEndDate())) {
            errors.add("Voucher is expired");
        }


        if (voucher.getMaxUsage() != null && voucher.getTimesUsed() >= voucher.getMaxUsage()) {
            errors.add("Voucher run out of applications");
        }

        if (userId != null && userId > 0) {
            Optional<User> user = userRepository.findByIdAndActive(userId, true);
            if (user.isPresent()) {
                List<Order> orders = orderRepository.findByUserId(userId);
                double total = 0.0;
                for (Order order : orders) {
                    total += order.getOrderDetails().stream().mapToDouble(e -> e.getPrice() * e.getNumberOfProduct()).sum();
                }
                BigDecimal doubleAsBigDecimal = BigDecimal.valueOf(total);

                int comparisonResult = doubleAsBigDecimal.compareTo(voucher.getMinPurchaseAmount());
                if (comparisonResult < 0) {
                    errors.add("You have not reached the voucher's minimum spend requirement");
                }
            }
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Get Voucher is not successful")
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .build());
        }

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get Voucher is successful")
                .status(HttpStatus.OK)
                .data(VoucherResponse.fromVoucher(voucher)).build());
    }

}
