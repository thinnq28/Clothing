package com.datn.shop_app.repository;

import com.datn.shop_app.entity.PromotionVariant;
import com.datn.shop_app.entity.Variant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionVariantRepository extends JpaRepository<PromotionVariant, Integer> {
    List<PromotionVariant> findPromotionVariantByPromotionIdAndVariantId(int promotionId, int variantId);

    @Query("SELECT p FROM PromotionVariant p WHERE" +
            "(:promotion_id IS NULL OR  p.promotion.id = :promotion_id) ")
    Page<PromotionVariant> findAllPromotionVariants(@Param("promotion_id") Integer promotionId, Pageable pageable);
}