package com.datn.shop_app.repository;

import com.datn.shop_app.entity.Option;
import com.datn.shop_app.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionRepository extends JpaRepository<Option, Integer> {
    @Query("SELECT o FROM Option o WHERE" +
            "(:name IS NULL OR :name = '' OR o.optionName LIKE %:name%) " +
            "AND (:active IS NULL OR o.active = :active)")
    Page<Option> findAllOptions(@Param("name") String name, @Param("active") Boolean active, Pageable pageable);

    @Query("SELECT o FROM Option o WHERE" +
            "(:name IS NULL OR :name = '' OR o.optionName LIKE %:name%) " +
            "AND (:active IS NULL OR o.active = :active)")
    List<Option> findAllOptions(@Param("name") String name, @Param("active") Boolean active);

    Optional<Option> findByIdAndActive(Integer id, Boolean active);

    @Query("SELECT o FROM Option o WHERE o.id IN :optionIds AND o.active = :active")
    List<Option> findOptionByIds(@Param("optionIds") List<Integer> optionIds, @Param("active") Boolean active);
}