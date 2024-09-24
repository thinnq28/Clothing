package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.CartItemDTO;
import com.datn.shop_app.DTO.OrderDTO;
import com.datn.shop_app.DTO.UpdateStatusOrderDTO;
import com.datn.shop_app.entity.*;
import com.datn.shop_app.repository.*;
import com.datn.shop_app.response.order.OrderResponse;
import com.datn.shop_app.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
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
    private final VoucherRepository voucherRepository;
    private final VariantRepository variantRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final VoucherOrderRepository voucherOrderRepository;

    @Override
    public Page<OrderResponse> getOrders(String fullName, String phoneNumber, String email, LocalDate orderDate, String status, Boolean active, Pageable pageable) {
        Page<Order> orders = orderRepository.findAllOder(fullName, phoneNumber, email, orderDate, status, active, pageable);
        return orders.map(OrderResponse::fromOrder);
    }

    @Override
    public Order updateStatus(Integer id, UpdateStatusOrderDTO orderDTO) {
        Order order = orderRepository.findByIdAndActive(id, true);
        if (order != null) {
            order.setStatus(orderDTO.getStatus());
            orderRepository.save(order);
            return order;
        }

        return null;
    }

    @Override
    public Order getOrderById(Integer id) {
        return orderRepository.findByIdAndActive(id, true);
    }

    @Override
    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        order.setOrderDate(LocalDate.now());
        if (orderDTO.getUserId() != null) {
            Optional<User> user = userRepository.findById(orderDTO.getUserId());
            user.ifPresent(order::setUser);
        }

        order.setActive(true);
        order.setTotal(orderDTO.getTotal());
        order = orderRepository.save(order);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItem : orderDTO.getCartItems()) {
             Optional<Variant> variant = variantRepository.findByIdAndActive(cartItem.getVariantId(), true);
             if(variant.isPresent()) {
                 variant.get().setQuantity(variant.get().getQuantity() - cartItem.getQuantity());
                 variantRepository.save(variant.get());

                 OrderDetail orderDetail = new OrderDetail();
                 orderDetail.setOrder(order);
                 orderDetail.setVariant(variant.get());
                 orderDetail.setPrice(variant.get().getPrice().doubleValue());
                 orderDetail.setNumberOfProduct(cartItem.getQuantity());
                 orderDetails.add(orderDetail);
             }
        }

        orderDetailRepository.saveAll(orderDetails);

        if (orderDTO.getCodes() != null && !orderDTO.getCodes().isEmpty()) {
            List<VoucherOrder> voucherOrders = new ArrayList<>();
            for (String code : orderDTO.getCodes()) {
                Voucher voucher = voucherRepository.findByCodeAndActive(code, true);
                if(voucher != null) {

                    voucher.setTimesUsed(voucher.getTimesUsed() + 1);
                    voucherRepository.save(voucher);

                    VoucherOrder voucherOrder = new VoucherOrder();
                    voucherOrder.setOrder(order);
                    voucherOrder.setVoucher(voucher);
                    voucherOrders.add(voucherOrder);
                }
            }

            voucherOrderRepository.saveAll(voucherOrders);
        }

        return order;
    }

    @Override
    public List<String> validOrder(OrderDTO orderDTO, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }
        }

        if(orderDTO.getEmail() != null && !orderDTO.getEmail().isEmpty() && !orderDTO.getEmail().matches("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")) {
            errors.add("Pattern of email is not correct");
        }

        List<CartItemDTO> cartItems = orderDTO.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            errors.add("Card item is not empty");
        } else {
            List<Integer> variantIds = cartItems.stream().map(CartItemDTO::getVariantId).toList();
            for (Integer variantId : variantIds) {
                Optional<Variant> variant = variantRepository.findByIdAndActive(variantId, true);
                if (variant.isEmpty()) {
                    errors.add("Some variants no longer exist");
                } else  {
                    if(variant.get().getQuantity() <= 0) errors.add("Some variants are out of stock.");
                }
            }
        }

        if (orderDTO.getUserId() != null && orderDTO.getUserId() > 0) {
            Optional<User> user = userRepository.findById(orderDTO.getUserId());
            if (user.isEmpty()) {
                errors.add("User not found");
            }
        }

        if (orderDTO.getCodes() != null && !orderDTO.getCodes().isEmpty()) {
            List<Voucher> vouchers = voucherRepository.getVouchersByCode(orderDTO.getCodes());
            for (Voucher voucher : vouchers) {
                if (voucher == null) {
                    errors.add("Voucher " + voucher.getCode() + " is not found");
                } else if (LocalDate.now().isBefore(voucher.getStartDate())) {
                    errors.add("Voucher " + voucher.getCode() + " has not been applied yet");
                } else if (LocalDate.now().isAfter(voucher.getEndDate())) {
                    errors.add("Voucher " + voucher.getCode() + " is expired");
                } else if (voucher.getMaxUsage() != null && voucher.getTimesUsed() > voucher.getMaxUsage()) {
                    errors.add("Voucher " + voucher.getCode() + " has already been applied");
                }

                if (orderDTO.getUserId() != null && orderDTO.getUserId() > 0) {
                    Optional<User> user = userRepository.findByIdAndActive(orderDTO.getUserId(), true);
                    if (user.isPresent()) {
                        List<Order> orders = orderRepository.findByUserId(orderDTO.getUserId());
                        BigDecimal total = orders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

//                        BigDecimal doubleAsBigDecimal = BigDecimal.valueOf(total);
                        int comparisonResult = total.compareTo(voucher.getMinPurchaseAmount());
                        if (comparisonResult < 0)
                            errors.add("You have not reached the voucher's " + voucher.getCode() + " minimum spend requirement");
                    }
                }
            }
        }

        if (!paymentMethods.contains(orderDTO.getPaymentMethod())) {
            errors.add("Payment method not supported");
        }

        if (!statuses.contains(orderDTO.getStatus())) {
            errors.add("Payment method not supported");
        }

        return errors;
    }
}
