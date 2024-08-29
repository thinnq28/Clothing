package com.datn.shop_app.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "options")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @ColumnDefault("''")
    @Column(name = "option_name")
    private String optionName;

    @ColumnDefault("1")
    @Column(name = "active")
    private Boolean active;

    @OneToMany(mappedBy = "option")
    @JsonManagedReference
    private List<OptionValue> optionValues;

    @OneToMany(mappedBy = "option")
    @JsonManagedReference
    private List<ProductOption> productOptions;

    @ColumnDefault("1")
    @Column(name = "is_multiple_usage")
    private Boolean isMultipleUsage;

}