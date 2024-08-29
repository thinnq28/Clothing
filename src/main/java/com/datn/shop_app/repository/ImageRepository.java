package com.datn.shop_app.repository;

import com.datn.shop_app.entity.Image;
import com.datn.shop_app.entity.OptionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    @Query("SELECT i FROM Image i WHERE i.id IN :imageIds")
    List<Image> findAllImages(@Param("imageIds") List<Integer> imageIds);
}