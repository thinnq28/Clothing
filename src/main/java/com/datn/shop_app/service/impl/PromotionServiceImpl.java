package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.PromotionDTO;
import com.datn.shop_app.entity.Promotion;
import com.datn.shop_app.repository.PromotionRepository;
import com.datn.shop_app.response.promotion.PromotionResponse;
import com.datn.shop_app.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;

    @Override
    public Page<PromotionResponse> getPromotions(String name, Boolean active, Pageable pageable) {
        Page<Promotion> promotions = promotionRepository.findAllPromotions(name, active, pageable);
        return promotions.map(PromotionResponse::fromPromotion);
    }

    @Override
    public List<String> validateInsertion(PromotionDTO promotionDTO, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }

            return errors;
        }

        if(promotionDTO.getStartDate().isEqual(promotionDTO.getEndDate())){
            errors.add("Start date cannot be after End date");
        }

        return errors;
    }

    @Override
    public Promotion save(PromotionDTO promotionDTO) {
        Promotion promotion = new Promotion();
        BeanUtils.copyProperties(promotionDTO, promotion);
        promotion.setActive(true);
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion update(Integer id, PromotionDTO promotionDTO) {
        Promotion promotion = promotionRepository.findById(id).orElse(null);
        if(promotion != null){
            BeanUtils.copyProperties(promotionDTO, promotion);
            return promotionRepository.save(promotion);
        }
        return null;
    }

    @Override
    public Promotion getPromotion(Integer promotionId) {
        return promotionRepository.findById(promotionId).orElse(null);
    }

    @Override
    public List<Promotion> getPromotionByIds(List<Integer> promotionIds) {
        return promotionRepository.getPromotionsByIds(promotionIds);
    }

    @Override
    public List<String> validateUpgrade(Integer id, PromotionDTO promotionDTO, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }

            return errors;
        }

        Promotion promotion = promotionRepository.findById(id).orElse(null);
        if(promotion == null){
            errors.add("Promotion is not found");
        }

        if(promotionDTO.getStartDate().isEqual(promotionDTO.getEndDate())){
            errors.add("Start date cannot be after End date");
        }

        return errors;
    }

    @Override
    public void delete(Integer id) {
        Promotion promotion = promotionRepository.findByIdAndActive(id, true);
        if(promotion != null){
            promotion.setActive(false);
            promotionRepository.save(promotion);
        }
    }

    @Override
    public List<Promotion> getPromotions(LocalDate endDate){
        return promotionRepository.getPromotionByEndDate(endDate, true);
    }

    @Override
    public void setActive(List<Promotion> promotions){
        for (Promotion promotion : promotions) {
                promotion.setActive(false);
        }

        promotionRepository.saveAll(promotions);
    }

}
