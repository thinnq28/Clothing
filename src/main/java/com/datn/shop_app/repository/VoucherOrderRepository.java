package com.datn.shop_app.repository;

import com.datn.shop_app.entity.VoucherOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherOrderRepository extends JpaRepository<VoucherOrder, Integer> {
}