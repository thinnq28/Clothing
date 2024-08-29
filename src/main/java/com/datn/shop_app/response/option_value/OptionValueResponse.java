package com.datn.shop_app.response.option_value;

import com.datn.shop_app.entity.OptionValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OptionValueResponse {
    @JsonProperty("id")
    private Integer optionValueId;

    @JsonProperty("optionId")
    private Integer optionId;

    @JsonProperty("name")
    private String optionValue;

    private Boolean active;

    private Integer totalPages;

    public static OptionValueResponse fromOptionValue(OptionValue optionValue) {
        OptionValueResponse optionValueResponse = new OptionValueResponse();
        optionValueResponse.setOptionId(optionValue.getOption().getId());
        optionValueResponse.setOptionValue(optionValue.getOptionValue());
        optionValueResponse.setActive(optionValue.getActive());
        optionValueResponse.setOptionValueId(optionValue.getId());
        return optionValueResponse;
    }
}
