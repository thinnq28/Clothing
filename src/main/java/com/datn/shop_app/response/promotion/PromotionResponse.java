package com.datn.shop_app.response.promotion;

import com.datn.shop_app.entity.Promotion;
import com.datn.shop_app.entity.PromotionVariant;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PromotionResponse {
    private Integer id;

    private String name;

    private BigDecimal discountPercentage;

    private Boolean active;

    private LocalDate startDate;

    private LocalDate endDate;

    public static PromotionResponse fromPromotion(Promotion promotion) {
        PromotionResponse promotionResponse = new PromotionResponse();
        BeanUtils.copyProperties(promotion, promotionResponse);
        return promotionResponse;
    }
}
