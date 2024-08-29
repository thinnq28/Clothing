package com.datn.shop_app.repository;

import com.datn.shop_app.entity.Order;
import com.datn.shop_app.entity.Variant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o WHERE" +
            "(:full_name IS NULL OR :full_name = '' OR o.fullName LIKE %:full_name%) " +
            "AND (:phone_number IS NULL OR :phone_number = '' OR o.phoneNumber LIKE %:phone_number%) " +
            "AND (:email IS NULL OR :email = '' OR o.email LIKE %:email%) " +
            "AND (:order_date IS NULL OR o.orderDate = :order_date)" +
            "AND (:status IS NULL OR :status = '' OR o.status LIKE %:status%) " +
            "AND (:active IS NULL OR o.active = :active)")
    Page<Order> findAllOder(@Param("full_name") String fullName,
                              @Param("phone_number") String phoneNumber,
                              @Param("email") String email,
                              @Param("order_date") LocalDate orderDate,
                              @Param("status") String status,
                              @Param("active") Boolean active, Pageable pageable);

    Order findByIdAndActive(Integer id, Boolean active);

    List<Order> findByUserId(Integer userId);
}