package com.datn.shop_app.repository;

import com.datn.shop_app.entity.VariantValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantValueRepository extends JpaRepository<VariantValue, Integer> {
}