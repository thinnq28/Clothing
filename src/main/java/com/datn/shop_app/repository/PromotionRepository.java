package com.datn.shop_app.repository;

import com.datn.shop_app.entity.Option;
import com.datn.shop_app.entity.Promotion;
import com.datn.shop_app.entity.Variant;
import com.datn.shop_app.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    @Query("SELECT p FROM Promotion p WHERE" +
            "(:name IS NULL OR :name = '' OR p.name LIKE %:name%) " +
            "AND (:active IS NULL OR p.active = :active)")
    Page<Promotion> findAllPromotions(@Param("name") String name, @Param("active") Boolean active, Pageable pageable);

    @Query("SELECT p FROM Promotion p WHERE p.id IN :promotionIds")
    List<Promotion> getPromotionsByIds(@Param("promotionIds") List<Integer> promotionIds);

    Promotion findByIdAndActive(Integer id, Boolean active);

    @Query("SELECT p FROM Promotion p WHERE " +
            "(:endDate IS NULL OR p.endDate <= :endDate)" +
            "AND (:active IS NULL OR p.active = :active)")
    List<Promotion> getPromotionByEndDate(@Param("endDate") LocalDate endDate,
                                          @Param("active") Boolean active);

}