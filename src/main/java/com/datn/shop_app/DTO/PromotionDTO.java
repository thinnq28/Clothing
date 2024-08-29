package com.datn.shop_app.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDTO {
    @NotNull(message = "Promotion Name cannot be null")
    @NotBlank(message = "Promotion Name cannot be blank")
    private String name;

    @NotNull(message = "Discount Percentage cannot be null")
    @PositiveOrZero(message = "Discount Percentage cannot be less than 0")
    @Max(value = 100, message = "Discount Percentage cannot be greater than 100")
    private BigDecimal discountPercentage;

    @NotNull(message = "Start Date cannot be null")
    @FutureOrPresent(message = "Start date cannot be a date in the past")
    private LocalDate startDate;

    @NotNull(message = "End Date cannot be null")
    @FutureOrPresent(message = "End date cannot be a date in the past")
    private LocalDate endDate;
}
