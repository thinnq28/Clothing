package com.datn.shop_app.repository;

import com.datn.shop_app.entity.Order;
import com.datn.shop_app.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {
    @Query("SELECT o FROM PurchaseOrder o WHERE" +
            "(:supplier_name IS NULL OR :supplier_name = '' OR o.supplier.supplierName LIKE %:supplier_name%) " +
            "AND (:order_date IS NULL OR o.orderDate = :order_date)")
    Page<PurchaseOrder> getPurchaseOrders(@Param("supplier_name") String supplierName,
                                  @Param("order_date") LocalDate orderDate,
                                  Pageable pageable);
}