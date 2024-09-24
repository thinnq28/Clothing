package com.datn.shop_app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @Size(max = 30)
    @NotNull
    @Column(name = "fullname", nullable = false, length = 30)
    private String fullName;

    @Size(max = 10)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 10)
    private String phoneNumber;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Size(max = 255)
    @Column(name = "note")
    private String note;

    @Lob
    @Column(name = "payment_method")
    private String paymentMethod;

    @Lob
    @Column(name = "status")
    private String status;

    @ColumnDefault("1")
    @Column(name = "active")
    private Boolean active;

    @OneToMany(mappedBy = "order")
    @JsonManagedReference
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "order")
    @JsonManagedReference
    private List<VoucherOrder> voucherOrders;

    @Column(name = "total", precision = 15, scale = 2)
    private BigDecimal total;

}