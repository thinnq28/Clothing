package com.datn.shop_app.response.option;

import com.datn.shop_app.response.supplier.SupplierResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListOptionResponse {
    private List<OptionResponse> options;
    private int totalPages;
}
