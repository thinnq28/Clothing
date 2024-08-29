package com.datn.shop_app.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionVariantDTO {
    private Integer optionId;
    private List<Integer> optionValueIds;
}
