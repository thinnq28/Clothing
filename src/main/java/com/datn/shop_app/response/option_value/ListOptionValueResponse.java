package com.datn.shop_app.response.option_value;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListOptionValueResponse {
    private List<OptionValueResponse> optionValues;
    private Integer totalPages;
}
