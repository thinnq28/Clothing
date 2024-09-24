package com.datn.shop_app.entity;

import com.datn.shop_app.model.ProductListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.context.event.EventListener;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "products")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(ProductListener.class)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @ColumnDefault("''")
    @Column(name = "product_name")
    private String productName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonBackReference
    private Supplier supplier;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commodity_id", nullable = false)
    private Commodity commodity;

    @Size(max = 1000)
    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Size(max = 255)
    @ColumnDefault("''")
    @Column(name = "description")
    private String description;

    @ColumnDefault("1")
    @Column(name = "active")
    private Boolean active;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<ProductOption> productOptions;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<Variant> variants;



}