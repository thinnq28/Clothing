package com.datn.shop_app.repository;

import com.datn.shop_app.entity.Option;
import com.datn.shop_app.entity.OptionValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionValueRepository extends JpaRepository<OptionValue, Integer> {
    @Query("SELECT o FROM OptionValue o WHERE " +
            "(:option_id IS NULL OR o.option.id = :option_id)" +
            "AND (:name IS NULL OR :name = '' OR o.optionValue LIKE %:name%) " +
            "AND (:active IS NULL OR o.active = :active)")
    Page<OptionValue> findAllOptionValues(@Param("option_id") Integer optionId, @Param("name") String name, @Param("active") Boolean active, Pageable pageable);

    Optional<OptionValue> findByIdAndActive(Integer id, Boolean active);

    @Query("SELECT o FROM OptionValue o WHERE o.id IN :optionValues")
    List<OptionValue> findAllOptionValues(@Param("optionValues") List<Integer> optionValues);
}