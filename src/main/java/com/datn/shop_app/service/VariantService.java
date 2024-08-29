package com.datn.shop_app.service;

import com.datn.shop_app.DTO.InsertVariantDTO;
import com.datn.shop_app.DTO.QuantityVariantDTO;
import com.datn.shop_app.DTO.UpdateVariantDTO;
import com.datn.shop_app.DTO.VariantDTO;
import com.datn.shop_app.entity.Image;
import com.datn.shop_app.entity.Variant;
import com.datn.shop_app.exception.DataNotFoundException;
import com.datn.shop_app.response.variant.VariantResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.List;

public interface VariantService {
    List<VariantResponse> getVariants(Integer productId);

    Page<VariantResponse> getVariants(String name, Boolean active, Pageable pageable);

    Page<VariantResponse> getVariants(String name, String productName, Boolean active, Pageable pageable);

    Page<VariantResponse> getVariants(String name, String productName, Integer promotionId, Boolean active, Pageable pageable);

    Variant getVariantById(Integer id) throws DataNotFoundException;

    List<VariantResponse> getVariantByIds(List<Integer> variantIds);

    void saveAllImage(List<Image> images);

    List<Variant> save(InsertVariantDTO variantDTO) throws DataNotFoundException;

    List<String> validateInsertion(InsertVariantDTO variantDTO, BindingResult bindingResult);

    List<String> validateUpdateQuantity(List<QuantityVariantDTO> variantDTOs);

    Integer updateQuantity(List<QuantityVariantDTO> variantDTOs);

    Variant update(Integer id, UpdateVariantDTO variantDTO) throws DataNotFoundException, IOException;

    List<String> validateUpgrade(Integer id, UpdateVariantDTO variantDTO, BindingResult bindingResult);

    @Transactional
    void delete(Integer id);
}
