package com.datn.shop_app.response.order;

import com.datn.shop_app.entity.Order;
import com.datn.shop_app.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.beans.BeanUtils;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderResponse {
    private Integer id;

    private String fullName;

    private String phoneNumber;

    private String email;

    private String address;

    private LocalDate orderDate;

    private String paymentMethod;

    private String status;

    private Boolean active;

    public static OrderResponse fromOrder(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        BeanUtils.copyProperties(order, orderResponse);
        return orderResponse;
    }
}
