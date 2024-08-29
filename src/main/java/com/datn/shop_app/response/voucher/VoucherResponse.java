package com.datn.shop_app.response.voucher;

import com.datn.shop_app.entity.Voucher;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherResponse {
    private Integer id;

    private String code;

    private String description;

    private BigDecimal discount;

    private String discountType;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal minPurchaseAmount;

    private BigDecimal maxDiscountAmount;

    private Integer maxUsage;

    private Integer timesUsed;

    private Boolean active;

    public static VoucherResponse fromVoucher(Voucher voucher) {
        VoucherResponse response = new VoucherResponse();
        BeanUtils.copyProperties(voucher, response);
        return response;
    }
}
