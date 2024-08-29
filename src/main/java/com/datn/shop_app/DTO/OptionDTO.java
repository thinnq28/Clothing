package com.datn.shop_app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionDTO {

    private Integer optionId;

    private String optionName;

    private Boolean isCreateNew;

    private Boolean isMultipleUsage;

    private List<String> optionValues;
}
