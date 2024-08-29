package com.datn.shop_app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "vouchers")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Voucher extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "discount", nullable = false, precision = 5, scale = 2)
    private BigDecimal discount;

    @NotNull
    @Lob
    @Column(name = "discount_type", nullable = false)
    private String discountType;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ColumnDefault("0.00")
    @Column(name = "min_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minPurchaseAmount;

    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "max_usage")
    private Integer maxUsage;

    @ColumnDefault("0")
    @Column(name = "times_used")
    private Integer timesUsed;

    @ColumnDefault("1")
    @Column(name = "active")
    private Boolean active;

    @OneToMany(mappedBy = "voucher")
    private List<VoucherOrder> voucherOrders;

}