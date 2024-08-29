package com.datn.shop_app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "variants")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Variant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    @Size(max = 255)
    @NotNull
    @Column(name = "sku_id", nullable = false)
    private String skuId;

    @Size(max = 255)
    @ColumnDefault("''")
    @Column(name = "variant_name")
    private String variantName;

    @ColumnDefault("0")
    @Column(name = "quantity")
    private Integer quantity;

    @ColumnDefault("0")
    @Column(name = "price")
    private Float price;

    @ColumnDefault("1")
    @Column(name = "active")
    private Boolean active;

    @OneToMany(mappedBy = "variant")
    @JsonManagedReference
    private List<Image> images;

    @OneToMany(mappedBy = "variant")
    @JsonManagedReference
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "variant")
    @JsonManagedReference
    private List<PromotionVariant> promotionVariants;

    @OneToMany(mappedBy = "variant")
    @JsonManagedReference
    private List<VariantValue> variantValues;

}