package com.datn.shop_app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionValueDTO {
    @NotNull(message = "Option value id cannot null")
    private Integer optionValueId;

    @Size(max = 100, message = "Option value has max length 255")
    @NotNull(message = "Option value cannot be null")
    @NotBlank(message = "Option value cannot be blank")
    private String optionValueName;

    @NotNull(message = "Option id cannot null")
    private Integer optionId;
}
