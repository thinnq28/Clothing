package com.datn.shop_app.repository;

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
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    @Query("SELECT v FROM Voucher v WHERE" +
            "(:code IS NULL OR :code = '' OR v.code LIKE %:code%) " +
            "AND (:startDate IS NULL OR v.startDate >= :startDate)" +
            "AND (:endDate IS NULL OR v.endDate <= :endDate)" +
            "AND (:active IS NULL OR v.active = :active)")
    Page<Voucher> findAllVouchers(@Param("code") String code, @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate,
                                  @Param("active") Boolean active, Pageable pageable);

    Voucher findByCodeAndActive(String code, Boolean active);

    Voucher findByIdAndActive(Integer id, Boolean active);

    @Query("SELECT v FROM Voucher v WHERE " +
            "(:endDate IS NULL OR v.endDate <= :endDate)" +
            "AND (:active IS NULL OR v.active = :active)")
    List<Voucher> getVoucherByEndDate(@Param("endDate") LocalDate endDate,
                                          @Param("active") Boolean active);

    Voucher getVoucherByCodeAndActive(String code, Boolean active);
}