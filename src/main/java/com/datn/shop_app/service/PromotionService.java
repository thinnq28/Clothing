package com.datn.shop_app.service;

import com.datn.shop_app.DTO.PromotionDTO;
import com.datn.shop_app.entity.Promotion;
import com.datn.shop_app.response.promotion.PromotionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

public interface PromotionService {
    Page<PromotionResponse> getPromotions(String name, Boolean active, Pageable pageable);

    List<String> validateInsertion(PromotionDTO promotionDTO, BindingResult bindingResult);

    Promotion save(PromotionDTO promotionDTO);

    Promotion update(Integer id, PromotionDTO promotionDTO);

    Promotion getPromotion(Integer promotionId);

    List<Promotion> getPromotionByIds(List<Integer> promotionIds);

    List<String> validateUpgrade(Integer id, PromotionDTO promotionDTO, BindingResult bindingResult);

    void delete(Integer id);

    List<Promotion> getPromotions(LocalDate endDate);

    void setActive(List<Promotion> promotions);
}
