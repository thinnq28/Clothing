package com.datn.shop_app.service;

import com.datn.shop_app.DTO.OrderDTO;
import com.datn.shop_app.DTO.UpdateStatusOrderDTO;
import com.datn.shop_app.entity.Order;
import com.datn.shop_app.response.order.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    Page<OrderResponse> getOrders(String fullName, String phoneNumber, String email, LocalDate orderDate, String status, Boolean active, Pageable pageable);

    Order updateStatus(Integer id, UpdateStatusOrderDTO orderDTO);

    Order getOrderById(Integer id);

    Order createOrder(OrderDTO orderDTO);

    List<String> validOrder(OrderDTO orderDTO, BindingResult bindingResult);
}
