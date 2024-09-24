package com.datn.shop_app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "commodities")
public class Commodity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commodity_id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "commodity_name", nullable = false, length = 100)
    private String commodityName;

    @ColumnDefault("1")
    @Column(name = "active")
    private Boolean active;

}