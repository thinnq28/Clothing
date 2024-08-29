package com.datn.shop_app.repository;

import com.datn.shop_app.entity.Commodity;
import com.datn.shop_app.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommodityRepository extends JpaRepository<Commodity, Integer> {
  Optional<Commodity> findByIdAndActive(Integer id, Boolean active);

  @Query("SELECT c FROM Commodity c WHERE" +
          "(:name IS NULL OR :name = '' OR c.commodityName LIKE %:name%) " +
          "AND (:active IS NULL OR c.active = :active)")
  Page<Commodity> findAllCommodities(@Param("name") String name, @Param("active") Boolean active, Pageable pageable);

  @Query("SELECT c FROM Commodity c WHERE" +
          "(:name IS NULL OR :name = '' OR c.commodityName LIKE %:name%) " +
          "AND (:active IS NULL OR c.active = :active)")
  List<Commodity> findAllCommodities(@Param("name") String name, @Param("active") Boolean active);

  Commodity findByIdAndActive(Integer id, boolean active);
}