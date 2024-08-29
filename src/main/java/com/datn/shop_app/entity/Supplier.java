package com.datn.shop_app.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "suppliers")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Supplier extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @ColumnDefault("''")
    @NotNull
    @Column(name = "supplier_name")
    private String supplierName;

    @Size(max = 10)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 10)
    private String phoneNumber;

    @Size(max = 255)
    @ColumnDefault("''")
    @Column(name = "address")
    private String address;

    @Size(max = 100)
    @ColumnDefault("''")
    @Column(name = "email", length = 100)
    private String email;

    @ColumnDefault("1")
    @Column(name = "active")
    private Boolean active;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Product> products;

}