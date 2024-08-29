package com.datn.shop_app.response.option;


import com.datn.shop_app.entity.Option;
import com.datn.shop_app.entity.OptionValue;
import com.datn.shop_app.entity.Supplier;
import com.datn.shop_app.response.option_value.OptionValueResponse;
import com.datn.shop_app.response.supplier.SupplierResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OptionResponse {
    private Integer id;
    @JsonProperty("name")
    private String optionName;
    private Boolean active;
    private int totalPages;
    private Boolean isMultipleUsage;
    private List<OptionValueResponse> optionValues;

    public static OptionResponse fromOption(Option option) {
        OptionResponse optionResponse = new OptionResponse();
        BeanUtils.copyProperties(option, optionResponse);
        List<OptionValueResponse> optionValues = new ArrayList<>();
        if (option.getOptionValues() != null &&  !option.getOptionValues().isEmpty()) {
            for (OptionValue optionValue : option.getOptionValues()) {
                if (optionValue.getActive()) {
                    OptionValueResponse optionValueResponse = OptionValueResponse.fromOptionValue(optionValue);
                    optionValues.add(optionValueResponse);
                }
            }
            optionResponse.setOptionValues(optionValues);
        }
        return optionResponse;
    }
}
