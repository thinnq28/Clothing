package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.OrderDTO;
import com.datn.shop_app.DTO.UpdateStatusOrderDTO;
import com.datn.shop_app.entity.Order;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.repository.OrderRepository;
import com.datn.shop_app.repository.UserRepository;
import com.datn.shop_app.response.order.OrderResponse;
import com.datn.shop_app.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    final List<String> paymentMethods = List.of("cod", "other");
    final List<String> statuses = List.of("pending", "processing", "shipped", "delivered");
    private final UserRepository userRepository;

    @Override
    public Page<OrderResponse> getOrders(String fullName, String phoneNumber, String email, LocalDate orderDate, String status, Boolean active, Pageable pageable) {
        Page<Order> orders = orderRepository.findAllOder(fullName, phoneNumber, email, orderDate, status, active, pageable);
        return orders.map(OrderResponse::fromOrder);
    }

    @Override
    public Order updateStatus(Integer id, UpdateStatusOrderDTO orderDTO){
        Order order = orderRepository.findByIdAndActive(id, true);
        if(order != null){
            order.setStatus(orderDTO.getStatus());
            orderRepository.save(order);
            return order;
        }

        return null;
    }

    @Override
    public Order getOrderById(Integer id){
        return orderRepository.findByIdAndActive(id, true);
    }

//    public Order createOrder(OrderDTO orderDTO){
//        Order order = new Order();
//        BeanUtils.copyProperties(orderDTO, order);
//        order.setOrderDate(LocalDate.now());
//        if(orderDTO.getUserId() != null) {
//            Optional<User> user = userRepository.findById(orderDTO.getUserId());
//            user.ifPresent(order::setUser);
//        }
//        order.setActive(true);
//        order = orderRepository.save(order);
//
//
//
//    }

    @Override
    public List<String> validOrder(OrderDTO orderDTO, BindingResult bindingResult){
        List<String> errors = new ArrayList<>();
        if(bindingResult.hasErrors()){
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError : fieldErrors){
                errors.add(fieldError.getDefaultMessage());
            }
        }

        if(orderDTO.getUserId() != null){
            Optional<User> user = userRepository.findById(orderDTO.getUserId());
            if(user.isEmpty()){
                errors.add("User not found");
            }
        }

        if(!paymentMethods.contains(orderDTO.getPaymentMethod())){
            errors.add("Payment method not supported");
        }

        if(!statuses.contains(orderDTO.getStatus())){
            errors.add("Payment method not supported");
        }

        return errors;
    }
}
