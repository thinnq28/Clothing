package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.PromotionDTO;
import com.datn.shop_app.entity.Promotion;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.promotion.ListPromotionResponse;
import com.datn.shop_app.response.promotion.PromotionResponse;
import com.datn.shop_app.service.PromotionService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/promotions")
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_READ')")
    public ResponseEntity<ResponseObject> getOptions(@RequestParam(defaultValue = "") String name,
                                                     @RequestParam(defaultValue = "true", name = "active") Boolean active,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                     @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<PromotionResponse> promotionPage = promotionService.getPromotions(name, active, pageRequest);
        totalPages = promotionPage.getTotalPages();
        List<PromotionResponse> promotionResponses = promotionPage.getContent();

        ListPromotionResponse listPromotionResponse = ListPromotionResponse.builder()
                .promotions(promotionResponses)
                .totalPages(totalPages).build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get promotions successfully")
                .status(HttpStatus.OK)
                .data(listPromotionResponse)
                .build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CREATE')")
    public ResponseEntity<ResponseObject> createPromotion(@Valid @RequestBody PromotionDTO promotionDTO, BindingResult bindingResult){

        try {
            List<String> errors = promotionService.validateInsertion(promotionDTO, bindingResult);
            if(!errors.isEmpty()){
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Create promotion is not successful")
                        .status(HttpStatus.BAD_REQUEST)
                        .data(errors).build());
            }

            Promotion promotion = promotionService.save(promotionDTO);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Create promotion is successful")
                    .status(HttpStatus.OK)
                    .data(PromotionResponse.fromPromotion(promotion))
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Create promotion is not successful")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_READ')")
    public ResponseEntity<ResponseObject> getPromotionDetal(@PathVariable Integer id) {
        Promotion promotion = promotionService.getPromotion(id);

        if(promotion == null) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Promotion is not exist")
                    .status(HttpStatus.NOT_FOUND)
                    .data("").build());
        }

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get promotion is successful")
                .status(HttpStatus.OK)
                .data(PromotionResponse.fromPromotion(promotion)).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CREATE')")
    public ResponseEntity<ResponseObject> updatePromotion(@PathVariable Integer id, @Valid @RequestBody PromotionDTO promotionDTO, BindingResult bindingResult){

        try {
            List<String> errors = promotionService.validateUpgrade(id, promotionDTO, bindingResult);
            if(!errors.isEmpty()){
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Update promotion is not successful")
                        .status(HttpStatus.BAD_REQUEST)
                        .data(errors).build());
            }

            Promotion promotion = promotionService.update(id, promotionDTO);
            if(promotion == null) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Promotion is not found")
                        .status(HttpStatus.BAD_REQUEST)
                        .data(errors).build());
            }
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Update promotion is successful")
                    .status(HttpStatus.OK)
                    .data(PromotionResponse.fromPromotion(promotion))
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Update promotion is not successful")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELETE')")
    public ResponseEntity<ResponseObject> deleteSupplier(@PathVariable Integer id) {
        Promotion promotion = promotionService.getPromotion(id);
        if (promotion == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Promotion is not exist")
                    .status(HttpStatus.NOT_FOUND).build());
        }

        promotionService.delete(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Delete promotion is successful")
                .status(HttpStatus.OK)
                .data("").build());
    }
}
