package com.datn.shop_app.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity  implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @ColumnDefault("''")
    @Column(name = "fullname", length = 100)
    private String fullName;

    @Size(max = 10)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 10)
    private String phoneNumber;

    @Size(max = 200)
    @ColumnDefault("''")
    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "email", length = 100)
    @NotNull
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Size(max = 100)
    @NotNull
    @ColumnDefault("''")
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @ColumnDefault("1")
    @Column(name = "active")
    private Boolean active;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Order> orders;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Token> tokens;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<UserRole> userRoles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getRoleName().toUpperCase());
            authorities.add(simpleGrantedAuthority);
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}