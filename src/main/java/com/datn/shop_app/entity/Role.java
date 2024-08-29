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
@Table(name = "roles")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Integer id;

    @Size(max = 45)
    @ColumnDefault("''")
    @Column(name = "role_name", length = 45)
    private String roleName;

    @ColumnDefault("1")
    @Column(name = "active")
    private Boolean active;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<UserRole> userRoles;

}