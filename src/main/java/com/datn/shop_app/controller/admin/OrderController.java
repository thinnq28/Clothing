package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.OrderDTO;
import com.datn.shop_app.DTO.UpdateStatusOrderDTO;
import com.datn.shop_app.entity.Order;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.order.ListOrderResponse;
import com.datn.shop_app.response.order.OrderResponse;
import com.datn.shop_app.response.voucher.ListVoucherResponse;
import com.datn.shop_app.response.voucher.VoucherResponse;
import com.datn.shop_app.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_READ')")
    public ResponseEntity<ResponseObject> getAllVouchers(@RequestParam(defaultValue = "") String fullName,
                                                         @RequestParam(defaultValue = "") String phoneNumber,
                                                         @RequestParam(defaultValue = "") String email,
                                                         @RequestParam(defaultValue = "") LocalDate orderDate,
                                                         @RequestParam(defaultValue = "") String status,
                                                         @RequestParam(defaultValue = "true", name = "active") Boolean active,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<OrderResponse> orderPage = orderService.getOrders(fullName, phoneNumber, email, orderDate, status, active, pageRequest);
        totalPages = orderPage.getTotalPages();
        List<OrderResponse> orderResponses = orderPage.getContent();

        ListOrderResponse listOrderResponse = ListOrderResponse.builder()
                .orders(orderResponses)
                .totalPages(totalPages).build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get orders is successful")
                .status(HttpStatus.OK)
                .data(listOrderResponse)
                .build());

    }

    @PutMapping("/update-status/{code}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_UPDATE')")
    public ResponseEntity<ResponseObject> updateStatus(@PathVariable Integer id,
                                                       @Valid @RequestBody UpdateStatusOrderDTO orderDTO,
                                                       BindingResult bindingResult) {
        List<String> statuses = List.of("pending", "processing", "shipped", "delivered", "cancelled");
        List<String> errors = new ArrayList<>();

        Order order = orderService.getOrderById(id);
        if (order == null) {
            errors.add("Order is not found");
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .message("Update status for order is failed").build());
        }

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }
        }

        if (!statuses.contains(orderDTO.getStatus())) {
            errors.add("Status can only be one of the following: " + statuses.stream().collect(Collectors.joining(",")));
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .message("Update status for order is failed").build());
        }

        order = orderService.updateStatus(id, orderDTO);
        if (order == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Update status for order is failed").build());
        }

        OrderResponse orderResponse = OrderResponse.fromOrder(order);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(orderResponse)
                .message("Update status for order is successful").build());
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createOrder(@Valid @RequestBody OrderDTO orderDTO, BindingResult bindingResult){
        List<String> errors = orderService.validOrder(orderDTO, bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Create order is not successful")
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .build());
        }

        Order order = orderService.createOrder(orderDTO);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create order is successful")
                .status(HttpStatus.OK   )
                .data(OrderResponse.fromOrder(order))
                .build());
    }

}
