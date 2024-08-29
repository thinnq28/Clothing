package com.datn.shop_app.response.promotion;

import com.datn.shop_app.response.option.OptionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListPromotionResponse {
    private List<PromotionResponse> promotions;
    private int totalPages;
}
