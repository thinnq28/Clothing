package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.PromotionVariantDTO;
import com.datn.shop_app.entity.Promotion;
import com.datn.shop_app.entity.PromotionVariant;
import com.datn.shop_app.entity.Variant;
import com.datn.shop_app.repository.PromotionVariantRepository;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.service.PromotionService;
import com.datn.shop_app.service.VariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/promotion-variants")
public class PromotionVariantController {
    private final PromotionVariantRepository promotionVariantRepository;
    private final VariantService variantService;
    private final PromotionService promotionService;


    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CREATE')")
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody PromotionVariantDTO promotionVariantDTO, BindingResult bindingResult) {
        try {
            List<String> errors = new ArrayList<>();
            if (bindingResult.hasErrors()) {
                bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Add promotion for variant is not successful")
                        .status(HttpStatus.BAD_REQUEST)
                        .data(errors).build());
            }

            Variant variant = variantService.getVariantById(promotionVariantDTO.getVariantId());
            if (variant == null) {
                errors.add("Variant not found");
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Add promotion for variant is not successful")
                        .status(HttpStatus.BAD_REQUEST)
                        .data(errors).build());
            }

            Promotion promotion = promotionService.getPromotion(promotionVariantDTO.getPromotionId());

            if (promotion == null) {
                errors.add("Promotion not found");
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Add promotion for variant is not successful")
                        .status(HttpStatus.BAD_REQUEST)
                        .data(errors).build());
            }

            PromotionVariant promotionVariant = new PromotionVariant();
            promotionVariant.setVariant(variant);
            promotionVariant.setPromotion(promotion);
            promotionVariantRepository.save(promotionVariant);

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Add promotion for variant is successful")
                    .status(HttpStatus.OK)
                    .data(promotionVariant).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Add promotion for variant is not successful")
                    .status(HttpStatus.BAD_REQUEST).build());
        }
    }

    @DeleteMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELETE')")
    public ResponseEntity<ResponseObject> delete(@RequestParam("variant_id") Integer variantId,
                                                 @RequestParam("promotion_id") Integer promotionId) {
        try {
            List<PromotionVariant> promotionVariants = promotionVariantRepository.findPromotionVariantByPromotionIdAndVariantId(promotionId, variantId);
            if (promotionVariants == null || promotionVariants.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .build());
            }

            promotionVariantRepository.deleteAll(promotionVariants);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Delete promotion variant is successful")
                    .status(HttpStatus.OK)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Delete promotion for variant is not successful")
                    .status(HttpStatus.BAD_REQUEST).build());
        }
    }
}
