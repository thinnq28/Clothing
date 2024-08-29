package com.datn.shop_app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tokens")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "token", nullable = false)
    private String token;

    @Size(max = 50)
    @NotNull
    @Column(name = "token_type", nullable = false, length = 50)
    private String tokenType;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @NotNull
    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

    @Column(name = "is_mobile", columnDefinition = "TINYINT(1)")
    private boolean isMobile;

    @NotNull
    @Column(name = "expired", nullable = false)
    private Boolean expired = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Size(max = 255)
    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_expiration_date")
    private LocalDateTime refreshExpirationDate;

}