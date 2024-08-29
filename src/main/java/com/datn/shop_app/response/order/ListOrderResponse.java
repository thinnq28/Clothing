package com.datn.shop_app.response.order;


import com.datn.shop_app.response.option_value.OptionValueResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListOrderResponse {
    private List<OrderResponse> orders;
    private Integer totalPages;
}
