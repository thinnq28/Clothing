package com.datn.shop_app.DTO;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherDTO {
    @Size(max = 50, message = "Size of Code cannot exceed 50 characters")
    @NotNull(message = "Voucher code cannot be null")
    private String code;

    @Size(max = 255, message = "Size of description cannot exceed 50 characters")
    private String description;

    @NotNull(message = "Discount cannot be null")
    @Min(value = 0, message = "Discount amount must be greater than 0")
    private BigDecimal discount;

    @NotNull(message = "Discount type cannot be null")
    @NotBlank(message = "Discount type cannot be blank")
    private String discountType;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be current or future date")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @FutureOrPresent(message = "End date must be current or future date")
    private LocalDate endDate;

    @Digits(integer = 10, fraction = 2, message = "Min purchase amount must not exceed 10 digits in the integer part and 2 digits in the decimal part")
    @PositiveOrZero(message = "Min purchase amount must be greater than 0")
    private BigDecimal minPurchaseAmount;

    @Digits(integer = 10, fraction = 2, message = "Max discount amount must not exceed 10 digits in the integer part and 2 digits in the decimal part")
    @PositiveOrZero(message = "Max discount amount must be greater than 0")
    private BigDecimal maxDiscountAmount;

    @PositiveOrZero(message = "Max usage must be greater than 0")
    private Integer maxUsage;
}
