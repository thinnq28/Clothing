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
@Table(name = "option_values")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_value_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "option_id", nullable = false)
    @JsonBackReference
    private Option option;

    @Size(max = 100)
    @ColumnDefault("''")
    @Column(name = "option_value", length = 100)
    private String optionValue;

    @ColumnDefault("1")
    @Column(name = "active")
    private Boolean active;

    @OneToMany(mappedBy = "optionValue")
    @JsonManagedReference
    private List<VariantValue> variantValues;

}