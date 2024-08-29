package com.datn.shop_app.response.commodity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListCommodityResponse {
    private List<CommodityResponse> commodities;
    private int totalPages;
}
